from rest_framework import serializers
from .models import EmergencyContact
from .services import is_valid_e164


class EmergencyContactSerializer(serializers.ModelSerializer):
    user = serializers.StringRelatedField(read_only=True)

    class Meta:
        model = EmergencyContact
        fields = [
            'id',
            'user',
            'name',
            'phone',
            'email',
            'relationship',
            'is_primary',
            'created_at',
        ]
        read_only_fields = ['id', 'user', 'created_at']

    def validate_phone(self, value):
        if not is_valid_e164(value):
            raise serializers.ValidationError('Phone must be in E.164 format, ex: +5511999999999.')
        return value
