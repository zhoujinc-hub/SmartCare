import requests
from service.rule_engine import RuleEngine


class EventDispatcher:

    def __init__(self):
        self.rule_engine = RuleEngine()
        self.springboot_url = "http://localhost:8080/api/alert"  # 以后换云服务器

    def handle(self, result):
        print(f"[AI] received: {result}")

        # 1. 判断是否需要报警
        if self.rule_engine.should_alert(result):

            alert_data = self.build_alert(result)

            # 2. 发给SpringBoot（先mock）
            self.send_to_backend(alert_data)

        else:
            print("[AI] no alert triggered")

    def build_alert(self, result):
        return {
            "camera_id": result.camera_id,
            "event": result.event,
            "confidence": result.confidence,
            "elder_id": result.elder_id,
            "type": result.type
        }

    def send_to_backend(self, data):
        try:
            # 先用mock接口
            res = requests.post(self.springboot_url, json=data)
            print("[AI] alert sent:", res.status_code)

        except Exception as e:
            print("[AI] send failed:", e)