#! /usr/bin/python3

import rospy
import time
from geometry_msgs.msg import Twist

rospy.init_node("qwerty", anonymous = True)
pub = rospy.Publisher("/turtle1/cmd_vel", Twist, queue_size = 10)
msg = Twist()
msg.linear.x = 1.0
while not rospy.is_shutdown():
	
	pub.publish(msg)
	msg.linear.x += 1.0
	time.sleep(1)
