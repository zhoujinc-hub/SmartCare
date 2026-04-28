class FallDetector:

    def __init__(self, ratio_threshold=1.2, fall_frame=15):
        self.ratio_threshold = ratio_threshold
        self.fall_frame = fall_frame

        # 👇 每个人单独计数
        self.person_states = {}

    def fall_detect(self, person):

        person_id = person["id"]

        w = person["w"]
        h = person["h"]

        if w == 0 or h == 0:
            return False, 0, 0

        ratio = h / w

        # 初始化状态
        if person_id not in self.person_states:
            self.person_states[person_id] = 0

        # 🔥 连续帧判断
        if ratio < self.ratio_threshold:
            self.person_states[person_id] += 1
        else:
            # 🔥 加“缓冲机制”，避免抖动清零
            self.person_states[person_id] = max(
                0,
                self.person_states[person_id] - 1
            )

        count = self.person_states[person_id]

        fall_condition = count >= self.fall_frame

        return fall_condition, ratio, count