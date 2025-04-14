import sys
import time
import math
import random
import logging
from dds_utils import DDSNode, Twist, Pose, Spawn
from logic_code import TurtleMove

logging.basicConfig(level=logging.INFO)

class LabNode:
    def __init__(self, turtle_id):
        self.turtle_id = turtle_id
        self.target_id = turtle_id - 1
        
        if turtle_id < 2:
            raise ValueError("Turtle ID must be >=2")
        
        self.dds = DDSNode()
        self.follower = TurtleMove()
        
        # Инициализация топиков
        self.writer_spawn = self.dds.create_writer('spawn', Spawn)
        self.pose_reader = self.dds.create_reader(f'pose{self.turtle_id}', Pose)
        self.target_reader = self.dds.create_reader(f'pose{self.target_id}', Pose)
        self.cmd_writer = self.dds.create_writer(f'cmd_vel{self.turtle_id}', Twist)
        
        # Отправка спауна
        x = random.uniform(1.0, 9.0) * 50
        y = random.uniform(1.0, 9.0) * 50
        theta = random.uniform(0, 2*math.pi)
        spawn_msg = Spawn(self.turtle_id, x, y, theta)
        
        for _ in range(3):  # Повторная отправка
            self.writer_spawn.write(spawn_msg)
            time.sleep(0.2)
        
        logging.info(f"Turtle {self.turtle_id} spawned")
        self.control_loop()

    def control_loop(self):
        while True:
            current = next(self.pose_reader.take_iter(), None)
            target = next(self.target_reader.take_iter(), None)
            
            if current and target:
                linear, angular = self.follower.track_cmd_vel(current, target)
                self.cmd_writer.write(Twist(
                    self.turtle_id, linear, angular
                ))
            time.sleep(0.1)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python3 lab_node.py <turtle_id>")
        sys.exit(1)
    LabNode(int(sys.argv[1]))
