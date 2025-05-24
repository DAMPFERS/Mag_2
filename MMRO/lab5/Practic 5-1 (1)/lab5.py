import cv2
import numpy as np
def apply_canny(image_path, threshold1, threshold2):
    # Загрузка изображения в оттенках серого
    img = cv2.imread(image_path, 0)
    # Проверка успешной загрузки изображения
    if img is None:
        print(f"Ошибка: Не удалось загрузить изображение {image_path}")
        return
    # Применение алгоритма Canny для обнаружения границ
    edges = cv2.Canny(img, threshold1, threshold2)
    # Создание окна с результатами
    result = np.hstack((img, edges))
    # Вывод изображения с границами
    cv2.imshow(f'Original vs Canny (Thresholds: {threshold1}, {threshold2})', result)
    cv2.waitKey(0)
    cv2.destroyAllWindows()

# Основная часть программы
if __name__ == "__main__":
    image_path = "5-2.jpg"
    # Эксперимент 1: Низкие пороги (более чувствительный детектор)
    apply_canny(image_path, 50, 100)
    # Эксперимент 2: Средние пороги (баланс между шумом и детализацией)
    apply_canny(image_path, 100, 200)
    # Эксперимент 3: Высокие пороги (меньше шума, но пропускает слабые границы)
    apply_canny(image_path, 150, 250)