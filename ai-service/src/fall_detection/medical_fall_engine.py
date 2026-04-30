from fall_detection.medical_pose_analyzer import MedicalPoseAnalyzer
from fall_detection.temporal_buffer import TemporalBuffer


class MedicalFallEngine:

    def __init__(self):
        self.pose = MedicalPoseAnalyzer()
        self.temporal = TemporalBuffer()

    def process(self, person):

        keypoints = person.get("keypoints", None)
        pid = person["id"]

        pose_fall, angle = self.pose.is_falling_pose(keypoints)

        final_fall = self.temporal.update(pid, pose_fall)

        return final_fall, angle