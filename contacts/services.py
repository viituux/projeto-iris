import re


E164_REGEX = re.compile(r"^\+[1-9]\d{1,14}$")


def is_valid_e164(phone: str) -> bool:
    return bool(E164_REGEX.match(phone or ""))
