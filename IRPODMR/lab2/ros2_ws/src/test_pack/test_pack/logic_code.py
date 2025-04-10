import math
from geometry_msgs.msg import Twist

class TurtleMover:
    def __init__(self, max_speed=2.0):
        self.max_speed = max_speed

    def compute_cmd_vel(self, current_pose, target_pose):
        dx = target_pose.x - current_pose.x
        dy = target_pose.y - current_pose.y
        distance = math.sqrt(dx**2 + dy**2)

        desired_angle = math.atan2(dy, dx)
        angle_diff = self.normalize_angle(desired_angle - current_pose.theta)

        twist = Twist()
        twist.linear.x = min(1.0 * distance, self.max_speed)
        twist.angular.z = 1.0 * angle_diff
        return twist

    def normalize_angle(self, angle):
        while angle > math.pi:
            angle -= 2 * math.pi
        while angle < -math.pi:
            angle += 2 * math.pi
        return angle

