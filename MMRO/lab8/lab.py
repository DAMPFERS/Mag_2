import cv2
import numpy as np


in_data = int(input("Номер задания(1 / 2)?: "))

if in_data == 1:
      
    # Загружаем набор данных для распознавания лица 
    face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml') 
    
    # Загружаем изображение, на котором нужно найти лицо 
    img = cv2.imread('1.jpg')  
    # Преобразуем изображение в оттенки серого 
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)  
    # Используем фильтры Хаара для поиска лица на изображении 
    faces = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30))  
    # Отмечаем найденные лица на изображении 
    for (x, y, w, h) in faces:     
        cv2.rectangle(img, (x, y), (x+w, y+h), (0, 255, 0), 2)  
    # Выводим результат 
    cv2.imshow('img', img) 
    cv2.waitKey(0) 
    cv2.destroyAllWindows()
    
elif in_data == 2:
    # Загрузка видеофайла и создание объекта видеопотока
    video = cv2.VideoCapture('video.mp4')

    # Проверка, успешно ли открыт видеофайл
    if not video.isOpened():
        print("Ошибка: не удалось открыть видео")
    else:
        # Создание объекта фоновой вычитания
        fgbg = cv2.createBackgroundSubtractorMOG2()

        while True:
            # Чтение кадра видеопотока
            ret, frame = video.read()

            # Если кадр не был прочитан, выходим из цикла
            if not ret:
                break

            # Применение алгоритма вычитания фона
            fgmask = fgbg.apply(frame)

            # Поиск контуров на двоичной карте переднего плана
            contours, hierarchy = cv2.findContours(fgmask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

            # Если найдены контуры, находим контур с максимальной площадью
            if contours:
                max_contour = max(contours, key=cv2.contourArea)
                x, y, w, h = cv2.boundingRect(max_contour)
                cv2.rectangle(frame, (x, y), (x+w, y+h), (0, 255, 0), 2)

            # Отображение кадра со слежением за объектом
            cv2.imshow('frame', frame)

            # Если нажата клавиша 'q', закрыть окно
            if cv2.waitKey(30) & 0xFF == ord('q'):
                break

        # Освобождение ресурсов
        video.release()
        cv2.destroyAllWindows()
    
    
else: print("Error")