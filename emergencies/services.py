from contacts.models import EmergencyContact
from django.db import transaction

from .tasks import send_emergency_alert_task


def build_emergency_message(user_email: str, emergency) -> str:
    maps_url = f"https://www.google.com/maps?q={emergency.latitude},{emergency.longitude}"
    return (
        "ALERTA SOS - PROJETO IRIS\n\n"
        f"A usuaria {user_email} precisa de ajuda.\n\n"
        f"Localizacao: {maps_url}\n"
        f"Nota: {emergency.description or 'Sem descricao'}"
    )


def trigger_emergency_alerts(user, emergency):
    contacts = EmergencyContact.objects.filter(user=user).order_by("-is_primary", "name")
    message = build_emergency_message(user.email, emergency)

    for contact in contacts:
        transaction.on_commit(
            lambda emergency_id=emergency.id, contact_id=contact.id, msg=message: (
                send_emergency_alert_task.delay(emergency_id, contact_id, msg)
            )
        )

    return contacts.count()
