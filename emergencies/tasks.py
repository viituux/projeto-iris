import logging

from celery import shared_task
from django.db import transaction

from contacts.models import EmergencyContact, EmergencyNotification
from .integrations import EvolutionAPIClient
from .models import Emergency, EmergencyAudio

logger = logging.getLogger(__name__)


@shared_task(bind=True, max_retries=3, default_retry_delay=10)
def send_emergency_alert_task(self, emergency_id: int, contact_id: int, message: str):
    try:
        emergency = Emergency.objects.get(id=emergency_id)
        contact = EmergencyContact.objects.get(id=contact_id)

        client = EvolutionAPIClient()

        # Envia mensagem de texto com mapa
        client.send_message(phone=contact.phone, text=message)

        # Envia áudios associados
        audios = EmergencyAudio.objects.filter(emergency=emergency)
        for audio in audios:
            try:
                # Lê o conteúdo do arquivo para enviar como base64 via Evolution API
                with audio.audio_file.open('rb') as f:
                    content = f.read()
                    client.send_audio(
                        phone=contact.phone,
                        audio_content=content,
                        filename=f"audio_emergencia_{emergency.id}.mp4"
                    )
            except Exception as e:
                logger.error(f"Erro ao enviar áudio para contato {contact_id}: {e}")

        with transaction.atomic():
            EmergencyNotification.objects.create(
                emergency=emergency,
                contact=contact,
                message=message,
                status="sent",
            )
        return {"contact_id": contact_id, "status": "sent"}
    except Exception as exc:
        logger.exception("Failed to send alert", extra={"emergency_id": emergency_id, "contact_id": contact_id})
        if self.request.retries >= self.max_retries:
            EmergencyNotification.objects.create(
                emergency_id=emergency_id,
                contact_id=contact_id,
                message=message,
                status="failed",
            )
        raise self.retry(exc=exc)
