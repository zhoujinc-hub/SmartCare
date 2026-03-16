class FallDetector:

    def __init__(self, ratio_threshold=1.2):
        self.ratio_threshold = ratio_threshold

    def detect(self, person):

        w = person["w"]
        h = person["h"]

        if w == 0:
            return False

        ratio = h / w

        # 跌倒判断
        if ratio < self.ratio_threshold:
            return True

        return False