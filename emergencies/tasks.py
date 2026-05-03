import logging

from celery import shared_task
from django.db import transaction

from contacts.models import EmergencyContact, EmergencyNotification
from .integrations import EvolutionAPIClient
from .models import Emergency

logger = logging.getLogger(__name__)


@shared_task(bind=True, max_retries=3, default_retry_delay=10)
def send_emergency_alert_task(self, emergency_id: int, contact_id: int, message: str):
    try:
        emergency = Emergency.objects.get(id=emergency_id)
        contact = EmergencyContact.objects.get(id=contact_id)

        client = EvolutionAPIClient()
        client.send_message(phone=contact.phone, text=message)

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
