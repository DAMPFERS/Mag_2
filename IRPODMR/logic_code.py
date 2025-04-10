#! /usr/bin/python3

import math
from geometry_msgs.msg import Twist

class TurtleMove:

	def __init__(self, max_speed = 4.0):
		self.max_speed = max_speed
		
	def Track_Cmd_Vel(self, current_pose, target_pose):
		dx = target_pose.x - current_pose.x
		dy = target_pose.y - current_pose.y
		
		module = math.sqrt(dx*dx + dy*dy)
		
		angle = math.atan2(dy, dx)
		new_angle = self.NormalizeAngle(angle - current_pose.theta)
		
		twist = Twist()
		twist.linear.x = min(1.0 * module,self.max_speed)
		twist.angular.z = 1.0 * new_angle
		return twist
		
	def NormalizeAngle(self, angle):
		
		while angle > math.pi:
			angle -= 2*math.pi
		while angle < -math.pi:
			angle += 2*math.pi
		return angle
		
