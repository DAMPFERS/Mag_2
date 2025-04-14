import math

class TurtleMove:
    def __init__(self, max_speed=2.0):
        self.max_speed = max_speed
    
    def track_cmd_vel(self, current, target):
        dx = target.x - current.x
        dy = target.y - current.y
        distance = math.hypot(dx, dy)
        
        target_angle = math.atan2(dy, dx)
        angle_diff = self.normalize_angle(target_angle - current.theta)
        
        linear = min(1.5 * distance, self.max_speed)
        angular = 3.0 * angle_diff
        return (linear, angular)
    
    def normalize_angle(self, angle):
        while angle > math.pi: angle -= 2*math.pi
        while angle < -math.pi: angle += 2*math.pi
        return angle
