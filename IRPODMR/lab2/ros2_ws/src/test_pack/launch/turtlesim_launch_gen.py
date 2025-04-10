import os
import random
from launch import LaunchDescription
from launch_ros.actions import Node
from launch.actions import DeclareLaunchArgument
from launch.substitutions import LaunchConfiguration

def generate_launch_description():

    N = 30

    nodes = [
        Node(
            package='turtlesim',
            executable='turtlesim_node',
            name='turtlesim'
        ),
        Node(
            package='turtlesim',
            executable='turtle_teleop_key',
            name='teleop',
            remappings=[('/teleop/turtle1/cmd_vel', '/turtle1/cmd_vel')],
            prefix='xterm -e'
        )
    ]

    for i in range(2, N+2):
        follower_name = f"turtle{i}"
        target_name = f"turtle{i-1}"
        spawn_x = round(random.uniform(1.0, 9.0), 2)
        spawn_y = round(random.uniform(1.0, 9.0), 2)
        spawn_theta = round(random.uniform(0.0, 3.14), 2)
        speed = round(random.uniform(1.5, 4.5), 2)

        nodes.append(
            Node(
                package='test_pack',
                executable='lab2',
                name=f"lab2_{i}_node",
                parameters=[
                    {"follower_name": follower_name},
                    {"target_name": target_name},
                    {"speed": speed},
                    {"spawn_x": spawn_x},
                    {"spawn_y": spawn_y},
                    {"spawn_theta": spawn_theta}
                ]
            )
        )

    return LaunchDescription(nodes)

