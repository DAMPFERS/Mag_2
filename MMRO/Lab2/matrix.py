'''
Camera matrix:
[[967.14344374   0.         627.97034034]
 [  0.         955.95044418 485.51946794]
 [  0.           0.           1.        ]]
 Distortion coefficient:
[[ 4.77556228e-01 -3.21763262e+00 -1.17049346e-02 -8.82137621e-04
   5.99887709e+00]]
 Rotation Vectors:
(array([[-0.46136934],
       [ 0.3060573 ],
       [-3.08233592]]),)
 Translation Vectors:
(array([[0.49632255],
       [3.1399822 ],
       [8.81352381]]),)
'''

import cv2
import numpy as np

# Задаём параметры калибровки, полученные ранее:
camera_matrix = np.array([[967.14344374,   0.        , 627.97034034],
                          [  0.        , 955.95044418, 485.51946794],
                          [  0.        ,   0.        ,   1.        ]])
dist_coeffs = np.array([[ 4.77556228e-01, -3.21763262e+00, -1.17049346e-02, -8.82137621e-04,
                          5.99887709e+00]])

# Загружаем исходное изображение с дисторсией
img = cv2.imread("5.jpg")  # замените "your_image.jpg" на имя вашего файла
h, w = img.shape[:2]

# Получаем оптимизированную новую матрицу камеры,
# которая может учитывать интересующую нас область изображения.
# new_camera_matrix, roi = cv2.getOptimalNewCameraMatrix(camera_matrix, dist_coeffs, (w, h), 1, (w, h))

# Получаем новую матрицу камеры с alpha=1 (сохраняем полный кадр)
new_camera_matrix, roi = cv2.getOptimalNewCameraMatrix(camera_matrix, dist_coeffs, (w, h), alpha=0.7, newImgSize=(w, h))

# Инициализируем карты для функции cv2.remap
mapx, mapy = cv2.initUndistortRectifyMap(camera_matrix, dist_coeffs, None, new_camera_matrix, (w, h), cv2.CV_32FC1)

# Выполняем репроекцию (исправление дисторсии)
# undistorted_img = cv2.remap(img, mapx, mapy, cv2.INTER_LINEAR)


# Затем выполняем remap без обрезки по ROI:
undistorted_img = cv2.remap(img, mapx, mapy, cv2.INTER_LINEAR)

# (Необязательно) Обрезаем изображение по ROI, если нужно
# x, y, w_roi, h_roi = roi
# undistorted_img = undistorted_img[y:y+h_roi, x:x+w_roi]

# Сохраняем и отображаем результат
cv2.imwrite("undistorted_image15.jpg", undistorted_img)
cv2.imshow("Undistorted Image", undistorted_img)
cv2.waitKey(0)
cv2.destroyAllWindows()
