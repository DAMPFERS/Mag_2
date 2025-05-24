import cv2
import numpy as np

def process_image(image_path, k_values):
    # Загружаем изображение
    img = cv2.imread(image_path)
    if img is None:
        print(f"Ошибка: Не удалось загрузить изображение {image_path}")
        return
    
    # Преобразуем изображение в массив NumPy и изменяем его форму
    pixel_vals = img.reshape((-1, 3))
    pixel_vals = np.float32(pixel_vals)
    
    # Параметры для кластеризации k-means
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 100, 0.85)
    
    for k in k_values:
        # Выполняем кластеризацию k-means
        _, labels, centers = cv2.kmeans(pixel_vals, k, None, criteria, 10, cv2.KMEANS_RANDOM_CENTERS)
        
        # Преобразуем центры кластеров в 8-битные значения
        centers = np.uint8(centers)
        
        # Создаем сегментированное изображение
        segmented_data = centers[labels.flatten()]
        segmented_img = segmented_data.reshape((img.shape))
        
        # Создаем маску с цветными сегментами
        labels_reshaped = labels.reshape((img.shape[0], img.shape[1]))
        mask = np.zeros_like(img)
        
        for i in range(k):
            mask[labels_reshaped == i] = centers[i]
        
        # Отображаем результаты
        window_name = f'{image_path} - K={k}'
        cv2.imshow(window_name, np.hstack([img, segmented_img, mask]))
        
        # Сохраняем результаты
        output_filename = f'output_{image_path.split(".")[0]}_k{k}.jpg'
        cv2.imwrite(output_filename, np.hstack([img, segmented_img, mask]))
        print(f"Результат сохранен как {output_filename}")

# Значения K для экспериментов
k_values = [2, 3, 5]

# Обрабатываем изображения
process_image('1.jpg', k_values)
process_image('2.jpg', k_values)

# Ждем нажатия клавиши и закрываем все окна
cv2.waitKey(0)
cv2.destroyAllWindows()