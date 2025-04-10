import math

class TurtleMove:
    def __init__(self, max_speed=4.0):
        self.max_speed = max_speed
    
    def track_cmd_vel(self, current_pose, target_pose):
        dx = target_pose.x - current_pose.x
        dy = target_pose.y - current_pose.y
        distance = math.sqrt(dx**2 + dy**2)
        
        angle = math.atan2(dy, dx)
        new_angle = self.normalize_angle(angle - current_pose.theta)
        
        return (min(2.0 * distance, self.max_speed), 2.5 * new_angle)
    
    def normalize_angle(self, angle):
        while angle > math.pi:
            angle -= 2 * math.pi
        while angle < -math.pi:
            angle += 2 * math.pi
        return angle
