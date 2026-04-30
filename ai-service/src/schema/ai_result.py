from pydantic import BaseModel
from typing import Optional


class AIResult(BaseModel):
    type: str  # registered_elder / unknown_person
    camera_id: str
    event: str  # fall_detected / normal / suspicious
    confidence: float

    elder_id: Optional[int] = None
    snapshot_url: Optional[str] = None