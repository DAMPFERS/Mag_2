import tkinter as tk
import math
from dds_utils import DDSNode, Twist, Pose, Spawn
from logic_code import TurtleMove  

class TurtleSimNode:
    
    def __init__(self):
        self.dds = DDSNode()
        self.writer_pose = self.dds.create_writer("pose")
        self.reader_cmd_vel = self.dds.create_reader("cmd_vel")
        self.reader_spawn = self.dds.create_reader("spawn")
        

        self.turtles = {}

        self.root = tk.Tk()
        self.root.title("Turtlesim Node")
        self.canvas = tk.Canvas(self.root, width=500, height=500, bg="white")
        self.canvas.pack()
        
        self.add_turtle(1, x=250, y=250, theta=0.0, color="green")
        
        self.follower = TurtleMove()  
        
        
        
        self.update_loop()
        self.root.mainloop()
    
    
    def add_turtle(self, turtle_id, x, y, theta, color="blue"):

        oval = self.canvas.create_oval(x-10, y-10, x+10, y+10, fill=color)

        pose = Pose(turtle_id=turtle_id, x=x/50, y=y/50, theta=theta)
        twist = Twist(turtle_id=turtle_id, linear_x=0.0, angular_z=0.0)
        self.turtles[turtle_id] = {"pose": pose, "twist": twist, "obj": oval, "color": color}
    

    
    def update_loop(self):

        cmd_samples = self.reader_cmd_vel.take()
        if cmd_samples:

            for sample in cmd_samples:
                twist = sample
                tid = twist.turtle_id
                if tid in self.turtles:
                    self.turtles[tid]["twist"] = twist
        

        spawn_samples = self.reader_spawn.take()
        if spawn_samples:
            for sample in spawn_samples:
                spawn_msg = sample
                if spawn_msg.turtle_id not in self.turtles:

                    self.add_turtle(spawn_msg.turtle_id, 
                                    x=spawn_msg.x, 
                                    y=spawn_msg.y, 
                                    theta=spawn_msg.theta, 
                                    color="red")
        

        for tid, data in self.turtles.items():
            pose = data["pose"]
            twist = data["twist"]
            if tid == 1:

                pose.theta += twist.angular_z * 0.1
                pose.x += twist.linear_x * 0.1 * math.cos(pose.theta)
                pose.y += twist.linear_x * 0.1 * math.sin(pose.theta)
            else:

                target = self.turtles.get(tid-1)
                if target:
                    linear, angular = self.follower.track_cmd_vel(pose, target["pose"])
                    pose.x += linear * 0.1 * math.cos(pose.theta)
                    pose.y += linear * 0.1 * math.sin(pose.theta)
                    pose.theta += angular * 0.1
            

            pose.x = max(0.0, min(10.0, pose.x))
            pose.y = max(0.0, min(10.0, pose.y))
            

            self.writer_pose.write(pose)
            

            x = pose.x * 50
            y = pose.y * 50
            self.canvas.coords(data["obj"], x-10, y-10, x+10, y+10)
            
        self.root.after(50, self.update_loop)

if __name__ == "__main__":
    TurtleSimNode()

