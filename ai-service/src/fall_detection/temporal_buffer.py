from collections import defaultdict, deque


class TemporalBuffer:

    def __init__(self, window_size=30):  # ~1-2秒（30fps）
        self.window_size = window_size
        self.buffer = defaultdict(lambda: deque(maxlen=window_size))

    def update(self, person_id, is_fall_score):

        self.buffer[person_id].append(is_fall_score)

        values = list(self.buffer[person_id])

        # 医疗级判断：70%帧数认为跌倒
        fall_ratio = sum(values) / len(values)

        return fall_ratio > 0.7