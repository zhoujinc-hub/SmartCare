from ultralytics import YOLO


class ModelManager:

    def __init__(self):
        self.models = {}

    def get_model(self, model_name):

        model_path = f"models/{model_name}"

        if model_name not in self.models:
            print(f"loading model: {model_name}")
            self.models[model_name] = YOLO(model_path)

        return self.models[model_name]