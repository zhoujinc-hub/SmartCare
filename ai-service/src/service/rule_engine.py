class RuleEngine:

    def should_alert(self, result) -> bool:
        """
        业务规则：不是AI决定报警，是规则决定
        """

        # 低置信度不报警
        if result.confidence < 0.6:
            return False

        # 摔倒才报警
        if result.event == "fall_detected":
            return True

        return False