#! /usr/bin/python3

import rospy
import time
from geometry_msgs.msg import Twist
from turtlesim.msg import Pose
from turtlesim.srv import Spawn

import logic_code

class TurtleNode:

	def __init__(self):
		self.follower_name = rospy.get_param("~follower_name", "turtle2")
		self.target_name = rospy.get_param("~target_name", "turtle1")
		self.speed = rospy.get_param("~speed", 1.0)
		self.spawn_x = rospy.get_param("~spawn_x", 2.0)
		self.spawn_y = rospy.get_param("~spawn_y", 6.0)
		self.spawn_theta = rospy.get_param("~spawn_theta", 0.0)
		
		self.mover = logic_code.TurtleMove(max_speed = self.speed)
		self.follower_pose = None
		self.target_pose = None
		
		if self.follower_name != "turtle1":
			rospy.wait_for_service("spawn")
			try:
				spawn_turtle = rospy.ServiceProxy("spawn", Spawn)
				resp = spawn_turtle(self.spawn_x, self.spawn_x, self.spawn_theta, self.follower_name)
				rospy.loginfo("A turtle has been created: %s", resp.name)
			except rospy.ServiceException as err:
				rospy.logerr("Error when calling the service \"spawn\": %s", err)
		
		self.pose_sub = rospy.Subscriber("/{}/pose".format(self.follower_name), Pose, self.Follower_Pose_Callback)
		self.target_pose_sub = rospy.Subscriber("/{}/pose".format(self.target_name), Pose, self.Target_Pose_Callback)
		self.cmd_vel_pub = rospy.Publisher("/{}/cmd_vel".format(self.follower_name), Twist, queue_size = 10)
		
	def Follower_Pose_Callback(self, msg):
		self.follower_pose = msg
		
	def Target_Pose_Callback(self, msg):
		self.target_pose = msg
		
	def run(self):
		rate = rospy.Rate(10)
		while not rospy.is_shutdown():
			if (self.follower_pose is not None) and (self.target_pose is not None):
				twist = self.mover.Track_Cmd_Vel(self.follower_pose, self.target_pose)
				self.cmd_vel_pub.publish(twist)
			rate.sleep()
			

rospy.init_node("turtle_node")
node = TurtleNode()
node.run()
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
