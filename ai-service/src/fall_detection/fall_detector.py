#业务逻辑：检测跌倒

class FallDetector:

    def __init__(self, ratio_threshold=1.2,fall_frame=20):
        self.ratio_threshold = ratio_threshold
        self.fall_frame = fall_frame
        self.fall_frame_count = 0

    def detect(self, person):

        w = person["w"]
        h = person["h"]

        if w == 0:
            return False

        ratio = h / w


        if ratio<self.ratio_threshold:
            self.fall_frame_count += 1
        else:#这里并未考虑干扰项，比如ratio受画面影响突然跳动到1.5，导致判断错误，count会瞬间清零
            self.fall_frame_count = 0
        # 跌倒判断
        fall_condition = self.fall_frame_count >= self.fall_frame


        return fall_condition, ratio