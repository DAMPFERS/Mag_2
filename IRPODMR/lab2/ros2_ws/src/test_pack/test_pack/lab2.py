import rclpy
from rclpy.node import Node
from turtlesim.srv import Spawn
from turtlesim.msg import Pose
from geometry_msgs.msg import Twist
from test_pack.logic_code import TurtleMover

class TurtleNode(Node):
    def __init__(self):
        super().__init__('lab2')

        self.declare_parameter('follower_name', 'turtle2')
        self.declare_parameter('target_name', 'turtle1')
        self.declare_parameter('speed', 2.0)
        self.declare_parameter('spawn_x', 5.0)
        self.declare_parameter('spawn_y', 5.0)
        self.declare_parameter('spawn_theta', 0.0)

        self.follower_name = self.get_parameter('follower_name').value
        self.target_name = self.get_parameter('target_name').value
        self.speed = self.get_parameter('speed').value
        self.spawn_x = self.get_parameter('spawn_x').value
        self.spawn_y = self.get_parameter('spawn_y').value
        self.spawn_theta = self.get_parameter('spawn_theta').value

        self.mover = TurtleMover(max_speed=self.speed)
        self.follower_pose = None
        self.target_pose = None

        if self.follower_name != "turtle1":
            self.spawn_turtle()

        self.create_subscription(Pose, f'/{self.follower_name}/pose', self.follower_pose_callback, 10)
        self.create_subscription(Pose, f'/{self.target_name}/pose', self.target_pose_callback, 10)
        self.cmd_vel_pub = self.create_publisher(Twist, f'/{self.follower_name}/cmd_vel', 10)
        self.create_timer(0.1, self.update_movement)

    def spawn_turtle(self):
        client = self.create_client(Spawn, 'spawn')
        while not client.wait_for_service(timeout_sec=1.0):
            self.get_logger().warn('Ожидание сервиса spawn...')
        
        request = Spawn.Request()
        request.x = self.spawn_x
        request.y = self.spawn_y
        request.theta = self.spawn_theta
        request.name = self.follower_name

        future = client.call_async(request)
        rclpy.spin_until_future_complete(self, future)
        if future.result():
            self.get_logger().info(f'Создана черепашка {self.follower_name}')
        else:
            self.get_logger().error('Ошибка при создании черепашки')

    def follower_pose_callback(self, msg):
        self.follower_pose = msg

    def target_pose_callback(self, msg):
        self.target_pose = msg

    def update_movement(self):
        if self.follower_pose and self.target_pose:
            twist = self.mover.compute_cmd_vel(self.follower_pose, self.target_pose)
            self.cmd_vel_pub.publish(twist)

def main():
    rclpy.init()
    node = TurtleNode()
    rclpy.spin(node)
    node.destroy_node()
    rclpy.shutdown()

if __name__ == '__main__':
    main()

