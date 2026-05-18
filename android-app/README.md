# Front Android (IRIS)

Estrutura pronta para abrir no Android Studio com:
- Tela de login (`/api/users/login/`)
- Tela principal com ações:
  - Listar contatos (`/api/contacts/`)
  - Listar pontos de apoio (`/api/locations/`)
  - Acionar SOS (`/api/emergencies/activate/`)

## Como executar

1. Abra a pasta `android-app/` no Android Studio.
2. Aguarde o sync do Gradle.
3. Rode o backend Django em `http://localhost:8000`.
4. Rode o app no emulador Android.

Por padrão, a URL da API está em `app/src/main/res/values/strings.xml`:

```xml
<string name="api_base_url">http://10.0.2.2:8000/</string>
```

`10.0.2.2` é o loopback do host para emulador Android.

## Observações

- O login usa `email` e `password` (compatível com seu `User.USERNAME_FIELD = 'email'`).
- Token JWT é salvo em `SharedPreferences` e enviado em `Authorization: Bearer <token>`.
