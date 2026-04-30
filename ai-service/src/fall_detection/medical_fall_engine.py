from .medical_pose_analyzer import MedicalPoseAnalyzer
from .temporal_buffer import TemporalBuffer


class MedicalFallEngine:

    def __init__(self):
        self.pose = MedicalPoseAnalyzer()
        self.temporal = TemporalBuffer()

    def process(self, person):
        keypoints = person.get("keypoints", None)
        pid = person["id"]

        pose_fall, angle = self.pose.is_falling_pose(keypoints)

        # ❗ 加入 angle 参与判断（关键）
        pose_fall = pose_fall or (angle < 110)

        final_fall = self.temporal.update(pid, pose_fall)

        return final_fall, angle