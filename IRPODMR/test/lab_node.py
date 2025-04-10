import sys
import time
import math
import random
from dds_utils import DDSNode, Twist, Pose, Spawn
from logic_code import TurtleMove

class LabNode:
    def __init__(self, turtle_id):
        self.turtle_id = turtle_id
        self.target_id = turtle_id - 1  # каждая нода преследует предыдущую черепашку
        
        self.dds = DDSNode()
        self.writer_spawn = self.dds.create_writer("spawn")
        self.writer_cmd_vel = self.dds.create_writer("cmd_vel")
        self.reader_pose = self.dds.create_reader("pose")
        self.follower = TurtleMove()

        # Генерация случайных начальных координат в диапазоне [1.0, 9.0] метров и угла (0, 2*pi)
        x = random.uniform(1.0, 9.0) * 50   # переводим в координаты холста (50 пикселей = 1 м)
        y = random.uniform(1.0, 9.0) * 50
        theta = random.uniform(0, 2 * math.pi)
        
        # Спавним черепашку с заданным turtle_id и случайным положением (в холсте – x, y; для логики делим на 50)
        spawn_msg = Spawn(turtle_id=self.turtle_id, x=x, y=y, theta=theta)
        self.writer_spawn.write(spawn_msg)
        print(f"Spawn sent for turtle {self.turtle_id} at x={x:.1f}, y={y:.1f}, theta={theta:.2f}")

        # Генерация случайной начальной скорости (все значения можно подобрать под условия симуляции)
        init_linear = random.uniform(-2.0, 2.0)
        init_angular = random.uniform(-1.0, 1.0)
        init_twist = Twist(turtle_id=self.turtle_id, linear_x=init_linear, angular_z=init_angular)
        self.writer_cmd_vel.write(init_twist)
        print(f"Initial twist sent for turtle {self.turtle_id}: linear_x={init_linear:.2f}, angular_z={init_angular:.2f}")

        # Небольшая задержка, чтобы нода turtlesim_node успела создать нужные объекты
        time.sleep(1)
        self.control_loop()


    def control_loop(self):
        while True:
            # Считываем все сообщения Pose для всех черепашек
            samples = self.reader_pose.take()
            pose_self = None
            pose_target = None
            if samples:
                for sample in samples:
                    pose = sample  # теперь sample – это непосредственно объект Pose
                    if pose.turtle_id == self.turtle_id:
                        pose_self = pose
                    elif pose.turtle_id == self.target_id:
                        pose_target = pose

            # Если для текущей черепашки и её цели получены позы, то вычисляем команду движения
            if pose_self and pose_target:
                linear, angular = self.follower.track_cmd_vel(pose_self, pose_target)
                twist = Twist(turtle_id=self.turtle_id, linear_x=linear, angular_z=angular)
                self.writer_cmd_vel.write(twist)
                # Можно выводить отладочную информацию:
                print(f"Turtle {self.turtle_id} chasing turtle {self.target_id}: linear={linear:.2f}, angular={angular:.2f}")
            time.sleep(0.05)

if __name__ == "__main__":
    # Ожидается, что в качестве аргумента передается turtle_id (целое число, >=2)
    if len(sys.argv) < 2:
        print("Usage: python3 lab_node.py <turtle_id>")
        sys.exit(1)
    try:
        tid = int(sys.argv[1])
        if tid < 2:
            print("turtle_id для lab_node должен быть >= 2")
            sys.exit(1)
    except ValueError:
        print("Неверный формат turtle_id")
        sys.exit(1)

    LabNode(tid)

