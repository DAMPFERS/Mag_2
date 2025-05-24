
k = 4
B = 9

# получим объект файла
with open("2-4.txt", "r") as file1:
    # итерация по строкам
    count_passengers = 0
    count_passengers_waiting = 0
    count_displacements = 0
    t_old = 0
    dt = 0
    t = 0
    
    
    dk = 0
    
    
    
    nodes =[0] * 5
    buses =[0] * B
    passangers = list()
    
    
    
    for i in range(len(nodes)):
            with open(f"node{i}.txt", "w") as save:
                pass
    for i in range(len(buses)):
            with open(f"bus{i}.txt", "w") as save:
                pass
  
    
    
    for line in file1:
        t += 1
        string = line.strip() 
        
        
        if ("Bus" in string ) and ("departing from node" in string ):
            pass
            count_displacements += 1
            dt += t - t_old
       
            s = string.split(' ')
            
            dk += float(s[-2]) / k
            
            
            buses[int(s[1])] = t - t_old
            
            t_old = t
                        
            with open(f"bus{int(s[1])}.txt", "a") as save:
                save.write(str(buses[int(s[1])]) + '\n')
            
            buses[int(s[1])] = t

        
        
        if ("Passenger" in string ) and ("created at node" in string ):
            # print(string )
            count_passengers  += 1
            count_passengers_waiting += 1
            s = string.split(' ')
            nodes[int(s[-3])] += 1
            # if s[1] != '0':
            passangers.append({"time":t, "status":0})

            
            
            
            
        elif ("Passenger" in string ) and ("arrived at destination" in string ):
            count_passengers_waiting -= 1
            s = string.split(' ')
            nodes[int(s[-1])] -= 1
            # print(passangers[int(s[1])])
            p = passangers[int(s[1])]
            p["time"] = t - p["time"]
            p["status"] = 1
            passangers[int(s[1])] = p
            # print(passangers[int(s[1])] )

            
            
            
        for i in range(len(nodes)):
            with open(f"node{i}.txt", "a") as save:
                save.write(str(abs(nodes[i])) + '\n')
        # for i in range(len(buses)):
        #     if buses[i] != 0:
        #         with open(f"bus{i}.txt", "a") as save:
        #             save.write(str(buses[i]) + '\n')
    
    print(f"Среднее dt: {dt / count_displacements}")
    print(f"Средняя загруженность: {dk / count_displacements}")


    with open(f"passangers.txt", "w") as save:
        # c = 0
        for i in passangers:
            if i["status"] == 1:
                save.write(str(i["time"]) + '\n')
                # c += 1
        print(f"Всего пассажиров: {count_passengers}")
        print(f"Всего пассажиров добралось: {count_passengers - count_passengers_waiting}")
        
            