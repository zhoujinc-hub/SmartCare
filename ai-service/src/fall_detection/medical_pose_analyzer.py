import numpy as np


class MedicalPoseAnalyzer:

    def __init__(self):
        pass

    def compute_angle(self, a, b, c):
        a = np.array(a)
        b = np.array(b)
        c = np.array(c)

        ba = a - b
        bc = c - b

        cos_angle = np.dot(ba, bc) / (
            np.linalg.norm(ba) * np.linalg.norm(bc) + 1e-6
        )

        return np.degrees(np.arccos(np.clip(cos_angle, -1.0, 1.0)))

    def is_falling_pose(self, keypoints):

        if keypoints is None:
            return False, 0

        # COCO keypoints:
        # 5 = left shoulder, 6 = right shoulder
        # 11 = left hip, 12 = right hip

        try:
            shoulder = (keypoints[5] + keypoints[6]) / 2
            hip = (keypoints[11] + keypoints[12]) / 2
            ankle = (keypoints[15] + keypoints[16]) / 2

            # 身体倾斜度（核心指标）
            angle = self.compute_angle(shoulder, hip, ankle)

            # 医疗级阈值
            is_fall = angle < 110  # 越小越躺

            return is_fall, angle

        except:
            return False, 90