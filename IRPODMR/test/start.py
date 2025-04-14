import subprocess
import time

N = 2

processes = []

processes.append(subprocess.Popen(["python3", "turtlesim_node.py"]))
time.sleep(1) 

processes.append(subprocess.Popen(["python3", "teleop_node.py"]))
time.sleep(1)


for i in range(2, N + 2):  
    processes.append(subprocess.Popen(["python3", "lab_node.py", str(i)]))
    time.sleep(2)


for p in processes:
    p.wait()
