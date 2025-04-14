import tkinter as tk
import math
import logging
import queue
import threading
from dds_utils import DDSNode, Twist, Pose, Spawn
from logic_code import TurtleMove

logging.basicConfig(level=logging.INFO)

class TurtleSimNode:


    def __init__(self):
        self.root = tk.Tk()
        self.root.title("TurtleSim")
        self.canvas = tk.Canvas(self.root, width=500, height=500, bg="white")
        self.canvas.pack()
        
        self.turtles = {}
        self.spawn_queue = queue.Queue()
        self.cmd_vel_queues = {}
        self.running = True
        
        # DDS инициализация в потоке
        self.dds = None
        threading.Thread(target=self._init_dds, daemon=True).start()
        
        # Первая черепашка
        #self._create_turtle(1, 250.0, 250.0, 0.0, "green")
        
        self.root.protocol("WM_DELETE_WINDOW", self.on_close)
        self.root.after(100, self.update)
        self.root.mainloop()


    def _init_dds(self):
        try:
            self.dds = DDSNode()
            self.spawn_reader = self.dds.create_reader('spawn', Spawn)
            threading.Thread(target=self._dds_listener, daemon=True).start()
            logging.info("DDS initialized")
        except Exception as e:
            logging.error(f"DDS init failed: {e}")
            self.root.after(0, self.on_close)


    def _dds_listener(self):
        while self.running:
            try:
                # Чтение спаун-сообщений
                for msg in self.spawn_reader.take_iter(timeout=0.1):
                    self.spawn_queue.put(msg)
                
                # Чтение команд управления
                for tid in list(self.turtles.keys()):
                    reader = self.turtles[tid]['cmd_vel_reader']
                    for twist in reader.take_iter(timeout=0):
                        if tid not in self.cmd_vel_queues:
                            self.cmd_vel_queues[tid] = queue.Queue()
                        self.cmd_vel_queues[tid].put(twist)
                        
            except Exception as e:
                logging.error(f"DDS error: {e}")


    def _create_turtle(self, tid, x, y, theta, color):
        try:
            # Создание топиков
            pose_topic = f'pose{tid}'
            cmd_topic = f'cmd_vel{tid}'
            
            writer = self.dds.create_writer(pose_topic, Pose)
            reader = self.dds.create_reader(cmd_topic, Twist)
            
            # GUI объект
            oval = self.canvas.create_oval(x-10, y-10, x+10, y+10, fill=color)
            
            self.turtles[tid] = {
                'pose_writer': writer,
                'cmd_vel_reader': reader,
                'pose': Pose(tid, x/50, y/50, theta),
                'twist': Twist(tid, 0.0, 0.0),
                'obj': oval
            }
            logging.info(f"Turtle {tid} created")
        except Exception as e:
            logging.error(f"Can't create turtle {tid}: {e}")

    def update(self):
        # Обработка спауна
        while not self.spawn_queue.empty():
            msg = self.spawn_queue.get()
            if msg.turtle_id not in self.turtles:
                self._create_turtle(
                    msg.turtle_id, 
                    msg.x, msg.y, 
                    msg.theta, "red"
                )
        
        # Обновление позиций
        for tid in list(self.turtles.keys()):
            data = self.turtles[tid]
            
            # Обновление команд
            if tid in self.cmd_vel_queues:
                while not self.cmd_vel_queues[tid].empty():
                    data['twist'] = self.cmd_vel_queues[tid].get()
            
            # Расчет движения
            if tid == 1:
                self._move_controlled_turtle(data)
            else:
                self._move_follower(tid, data)
            
            # Отрисовка
            x = data['pose'].x * 50
            y = data['pose'].y * 50
            self.canvas.coords(data['obj'], x-10, y-10, x+10, y+10)
            
        self.root.after(50, self.update)

    def _move_controlled_turtle(self, data):
        twist = data['twist']
        data['pose'].theta += twist.angular_z * 0.1
        data['pose'].x += twist.linear_x * 0.1 * math.cos(data['pose'].theta)
        data['pose'].y += twist.linear_x * 0.1 * math.sin(data['pose'].theta)
        data['pose'].x = max(0.0, min(10.0, data['pose'].x))
        data['pose'].y = max(0.0, min(10.0, data['pose'].y))
        data['pose_writer'].write(data['pose'])

    def _move_follower(self, tid, data):
        target = self.turtles.get(tid-1, {}).get('pose')
        if target:
            follower = TurtleMove()
            linear, angular = follower.track_cmd_vel(data['pose'], target)
            data['pose'].x += linear * 0.1 * math.cos(data['pose'].theta)
            data['pose'].y += linear * 0.1 * math.sin(data['pose'].theta)
            data['pose'].theta += angular * 0.1
            data['pose_writer'].write(data['pose'])

    def on_close(self):
        self.running = False
        self.root.destroy()

if __name__ == "__main__":
    TurtleSimNode()
