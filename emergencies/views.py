from rest_framework import status, viewsets
from rest_framework.decorators import action
from rest_framework.response import Response

from .models import Emergency
from .serializers import EmergencySerializer
from .services import trigger_emergency_alerts


class EmergencyViewSet(viewsets.ModelViewSet):
    serializer_class = EmergencySerializer

    def get_queryset(self):
        return Emergency.objects.filter(user=self.request.user).order_by("-created_at")

    @action(detail=False, methods=["post"], url_path="activate")
    def activate(self, request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        emergency = Emergency.objects.create(
            user=request.user,
            description=serializer.validated_data.get("description", "Botao SOS acionado"),
            status="active",
            latitude=serializer.validated_data["latitude"],
            longitude=serializer.validated_data["longitude"],
        )
        total_contacts = trigger_emergency_alerts(request.user, emergency)

        return Response(
            {
                "success": True,
                "message": "Alerta SOS ativado com sucesso. Envios em processamento.",
                "data": {
                    "emergency": self.get_serializer(emergency).data,
                    "contacts_queued": total_contacts,
                },
            },
            status=status.HTTP_201_CREATED,
        )
