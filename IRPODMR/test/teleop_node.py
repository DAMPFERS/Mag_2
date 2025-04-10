import tkinter as tk
from dds_utils import DDSNode, Twist

class TeleopNode:
    
    def __init__(self):
        self.dds = DDSNode()
        self.writer_cmd_vel = self.dds.create_writer("cmd_vel")
        
        self.root = tk.Tk()
        self.root.title("Teleop Node")
        self.root.bind("<KeyPress>", self.on_key_press)
        self.root.bind("<KeyRelease>", self.on_key_release)
        self.root.mainloop()
      
        
    def on_key_press(self, event):

        twist = Twist(turtle_id=1, linear_x=0.0, angular_z=0.0)
        if event.keysym == "Up":
            twist.linear_x = 2.0
        elif event.keysym == "Down":
            twist.linear_x = -2.0
        elif event.keysym == "Left":
            twist.angular_z = 1.0
        elif event.keysym == "Right":
            twist.angular_z = -1.0
        self.writer_cmd_vel.write(twist)
        
        
    def on_key_release(self, event):
        twist = Twist(turtle_id=1, linear_x=0.0, angular_z=0.0)
        self.writer_cmd_vel.write(twist)

if __name__ == "__main__":
    TeleopNode()

