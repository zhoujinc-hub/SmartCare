from pathlib import Path

from ultralytics import YOLO

# ai-service/models（与当前工作目录无关，避免在 src 下运行时找不到权重）
_MODELS_DIR = Path(__file__).resolve().parent.parent.parent / "models"


class ModelManager:

    def __init__(self):
        self.models = {}

    def get_model(self, model_name):

        model_path = _MODELS_DIR / model_name

        if model_name not in self.models:
            print(f"loading model: {model_name}")
            self.models[model_name] = YOLO(str(model_path))

        return self.models[model_name]