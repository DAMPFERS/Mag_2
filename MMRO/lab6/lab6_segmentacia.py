import cv2
import numpy as np

def process_image(image_path, threshold_values):
    # Загружаем изображение
    img = cv2.imread(image_path)
    if img is None:
        print(f"Ошибка: Не удалось загрузить изображение {image_path}")
        return
    
    # Преобразуем в оттенки серого
    gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    
    for threshold in threshold_values:
        # Применяем пороговую сегментацию
        _, thresh = cv2.threshold(gray_img, threshold, 255, cv2.THRESH_BINARY)
        
        # Морфологические операции
        kernel = np.ones((5,5), np.uint8)
        morph_img = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)
        
        # Находим контуры
        contours, _ = cv2.findContours(morph_img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        # Рисуем контуры
        segmented_img = img.copy()
        cv2.drawContours(segmented_img, contours, -1, (0, 255, 0), 2)
        
        # Создаем окно для отображения
        window_name = f'{image_path} - Threshold {threshold}'
        cv2.imshow(window_name, segmented_img)
        
        # Сохраняем результат
        output_filename = f'output_{image_path.split(".")[0]}_thr{threshold}.jpg'
        cv2.imwrite(output_filename, segmented_img)
        print(f"Результат сохранен как {output_filename}")

# Пороговые значения для экспериментов
thresholds = [100, 150]

# Обрабатываем изображения
process_image('1.jpg', thresholds)
process_image('2.jpg', thresholds)

# Ждем нажатия клавиши и закрываем все окна
cv2.waitKey(0)
cv2.destroyAllWindows()