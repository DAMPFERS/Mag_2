import tkinter as tk
import time
from dds_utils import DDSNode, Twist, Spawn

class TeleopNode:
    def __init__(self):
        self.dds = DDSNode()
        self.turtle_id = 1  # Управляем черепашкой 1
        
        # Спавним черепашку
        self._spawn_turtle()
        
        # Инициализация управления
        self.writer = self.dds.create_writer(f'cmd_vel{self.turtle_id}', Twist)
        self.root = tk.Tk()
        self.root.title("Teleop")
        self.root.bind("<KeyPress>", self.on_key_press)
        self.root.bind("<KeyRelease>", self.on_key_release)
        self.root.mainloop()

    def _spawn_turtle(self):
        """Отправка спаун-сообщения для черепашки 1"""
        writer = self.dds.create_writer('spawn', Spawn)
        
        # Стартовая позиция в центре
        spawn_msg = Spawn(
            turtle_id=self.turtle_id,
            x=250,  # 250px = 5.0 метров (500px/50=10m)
            y=250,
            theta=0.0
        )
        
        # Гарантированная отправка с повторами
        for _ in range(5):
            writer.write(spawn_msg)
            time.sleep(0.2)
        
    def on_key_press(self, event):
        twist = Twist(self.turtle_id, 0.0, 0.0)
        if event.keysym == "Up": twist.linear_x = 2.0
        elif event.keysym == "Down": twist.linear_x = -2.0
        elif event.keysym == "Left": twist.angular_z = 1.0
        elif event.keysym == "Right": twist.angular_z = -1.0
        self.writer.write(twist)

    def on_key_release(self, event):
        self.writer.write(Twist(self.turtle_id, 0.0, 0.0))

if __name__ == "__main__":
    TeleopNode()
