#! /usr/bin/python3

import rospy
import time
from geometry_msgs.msg import Twist

def callback(msg):
	rospy.loginfo(msg)
	
rospy.init_node("command_listener")
rospy.Subscriber("/turtle1/cmd_vel", Twist, callback)
rospy.spin()
