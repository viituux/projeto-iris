import requests
import base64
from django.conf import settings


class EvolutionAPIClient:
    def __init__(self):
        if not settings.EVOLUTION_API_URL:
            raise ValueError("EVOLUTION_API_URL is not configured.")
        if not settings.EVOLUTION_API_KEY:
            raise ValueError("EVOLUTION_API_KEY is not configured.")
        if not settings.EVOLUTION_API_INSTANCE:
            raise ValueError("EVOLUTION_API_INSTANCE is not configured.")

        self.base_url = settings.EVOLUTION_API_URL.rstrip("/")
        self.api_key = settings.EVOLUTION_API_KEY
        self.instance = settings.EVOLUTION_API_INSTANCE

    def send_message(self, phone: str, text: str) -> dict:
        url = f"{self.base_url}/message/sendText/{self.instance}"
        headers = {"apikey": self.api_key, "Content-Type": "application/json"}

        # Limpa o número para evitar erros de envio
        clean_phone = phone.replace("+", "").replace(" ", "").replace("-", "")

        payload = {
            "number": clean_phone,
            "textMessage": {"text": text},
        }
        response = requests.post(url, json=payload, headers=headers, timeout=20)
        response.raise_for_status()
        return response.json()

    def send_audio(self, phone: str, audio_content: bytes, filename: str = "emergency_audio.mp4") -> dict:
        url = f"{self.base_url}/message/sendMedia/{self.instance}"
        headers = {"apikey": self.api_key, "Content-Type": "application/json"}

        base64_audio = base64.b64encode(audio_content).decode('utf-8')

        # Limpa o número para evitar erros de envio
        clean_phone = phone.replace("+", "").replace(" ", "").replace("-", "")

        payload = {
            "number": clean_phone,
            "mediaMessage": {
                "mediatype": "audio",
                "mimetype": "audio/mp4",
                "media": base64_audio,
                "fileName": filename,
                "ptt": True
            }
        }
        response = requests.post(url, json=payload, headers=headers, timeout=40)
        response.raise_for_status()
        return response.json()
