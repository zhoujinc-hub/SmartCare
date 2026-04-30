from collections import defaultdict, deque


class TemporalBuffer:

    def __init__(self, window_size=30):
        self.window_size = window_size
        self.buffer = defaultdict(lambda: deque(maxlen=window_size))

    def update(self, person_id, is_fall):

        buf = self.buffer[person_id]
        buf.append(1 if is_fall else 0)

        values = list(buf)

        # 🔥 1. 连续触发检测（关键）
        consecutive = 0
        max_consecutive = 0

        for v in values:
            if v == 1:
                consecutive += 1
                max_consecutive = max(max_consecutive, consecutive)
            else:
                consecutive = 0

        # 🔥 2. 加权比例（后期权重更高）
        weights = [i / len(values) for i in range(len(values))]
        weighted_sum = sum(v * w for v, w in zip(values, weights))

        # 🔥 3. 融合判断
        return (max_consecutive >= 5) or (weighted_sum > 0.5)