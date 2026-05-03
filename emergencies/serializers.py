from rest_framework import serializers
from .models import Emergency, EmergencyAudio


class EmergencySerializer(serializers.ModelSerializer):
    user = serializers.StringRelatedField(read_only=True)

    class Meta:
        model = Emergency
        fields = [
            'id',
            'user',
            'description',
            'status',
            'latitude',
            'longitude',
            'created_at',
        ]
        read_only_fields = ['id', 'user', 'status', 'created_at']

    def validate_latitude(self, value):
        if value < -90 or value > 90:
            raise serializers.ValidationError("Latitude must be between -90 and 90.")
        return value

    def validate_longitude(self, value):
        if value < -180 or value > 180:
            raise serializers.ValidationError("Longitude must be between -180 and 180.")
        return value


class EmergencyAudioSerializer(serializers.ModelSerializer):
    class Meta:
        model = EmergencyAudio
        fields = ['id', 'audio_file', 'created_at']
        read_only_fields = ['id', 'created_at']
