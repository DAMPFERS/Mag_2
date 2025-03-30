import cv2
import numpy as np
import glob
import os

# Параметры калибровки, полученные ранее
camera_matrix = np.array([[925.66785216,   0.         , 641.67693632],
                          [  0.         , 924.31727727, 480.03799474],
                          [  0.         ,   0.         ,   1.        ]])
dist_coeffs = np.array([[ 0.2715328, -1.52664438, -0.00627077, 0.00302715, 2.23967052]])

# Создаем папку для сохранения исправленных изображений, если её еще нет
output_folder = "corrected_images"
if not os.path.exists(output_folder):
    os.makedirs(output_folder)

# Получаем список всех jpg-файлов в текущей папке
images = glob.glob("*.jpg")

for image_path in images:
    # Читаем изображение
    img = cv2.imread(image_path)
    if img is None:
        print(f"Не удалось прочитать файл {image_path}")
        continue

    h, w = img.shape[:2]

    # Вычисляем оптимальную новую матрицу камеры
    new_camera_matrix, roi = cv2.getOptimalNewCameraMatrix(camera_matrix, dist_coeffs, (w, h), 1, (w, h))

    # Исправляем искажения с помощью cv2.undistort
    undistorted_img = cv2.undistort(img, camera_matrix, dist_coeffs, None, new_camera_matrix)

    # Опционально: обрезаем изображение по ROI (region of interest)
    x, y, w_roi, h_roi = roi
    undistorted_img_cropped = undistorted_img[y:y+h_roi, x:x+w_roi]

    # Формируем имя для сохранения
    base_name = os.path.basename(image_path)
    output_path = os.path.join(output_folder, f"corrected_{base_name}")

    # Сохраняем исправленное изображение
    cv2.imwrite(output_path, undistorted_img_cropped)
    print(f"Сохранено исправленное изображение: {output_path}")
