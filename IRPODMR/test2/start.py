import subprocess
import time

def main():
    processes = []
    
    processes.append(subprocess.Popen(["python3", "turtlesim_node.py"]))
    time.sleep(5)  # Важно для инициализации GUI
    
    processes.append(subprocess.Popen(["python3", "teleop_node.py"]))
    time.sleep(2)
    
    for tid in range(2, 5):  # Создаем 3 преследователя
        processes.append(subprocess.Popen([
            "python3", "lab_node.py", str(tid)
        ]))
        time.sleep(1)
    
    try:
        for p in processes:
            p.wait()
    except KeyboardInterrupt:
        for p in processes:
            p.terminate()

if __name__ == "__main__":
    main()
