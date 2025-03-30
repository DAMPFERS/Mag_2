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


'''
 Camera matrix:
[[925.66785216   0.         641.67693632]
 [  0.         924.31727727 480.03799474]
 [  0.           0.           1.        ]]
 Distortion coefficient:
[[ 0.2715328  -1.52664438 -0.00627077  0.00302715  2.23967052]]
 Rotation Vectors:
(array([[ 0.18159418],
       [-0.16482584],
       [ 0.00087726]]), array([[ 0.39024234],
       [-0.29609408],
       [ 3.10294652]]), array([[ 0.18177088],
       [-0.0143284 ],
       [-0.00416343]]), array([[ 0.2054298 ],
       [-0.18500974],
       [ 0.0120065 ]]), array([[-0.41193345],
       [ 0.31582221],
       [-3.0867741 ]]))
 Translation Vectors:
(array([[-0.69807361],
       [-2.57016504],
       [ 7.60154506]]), array([[2.15694153],
       [3.38489163],
       [8.81716156]]), array([[-1.80807002],
       [-2.62532881],
       [ 7.62842664]]), array([[ 0.54198851],
       [-2.43122727],
       [ 7.88170558]]), array([[0.36778667],
       [3.18251824],
       [8.50908493]]))

'''
import cv2
import numpy as np

# Параметры после калибровки:
camera_matrix = np.array([[925.66785216,   0.         , 641.67693632],
                          [  0.         , 924.31727727, 480.03799474],
                          [  0.         ,   0.         ,   1.        ]])
dist_coeffs = np.array([[ 0.2715328, -1.52664438, -0.00627077, 0.00302715, 2.23967052]])

# СЧитывание исходного изображени 
img = cv2.imread("5.jpg")
h, w = img.shape[:2]

# Вычисление оптимальной новой матрицы камеры.
new_camera_matrix, roi = cv2.getOptimalNewCameraMatrix(camera_matrix, dist_coeffs, (w, h), 1, (w, h))

# --- Способ 1: Использование cv2.undistort ---
undistorted_img = cv2.undistort(img, camera_matrix, dist_coeffs, None, new_camera_matrix)

# Опционально: обрезка изображения по ROI (region of interest)
x, y, w_roi, h_roi = roi
undistorted_img_cropped = undistorted_img[y:y+h_roi, x:x+w_roi]

cv2.imshow("Undistorted Image", undistorted_img_cropped)
cv2.waitKey(0)

# --- Способ 2: Использование initUndistortRectifyMap и remap ---
map1, map2 = cv2.initUndistortRectifyMap(camera_matrix, dist_coeffs, None, new_camera_matrix, (w, h), cv2.CV_16SC2)
remapped_img = cv2.remap(img, map1, map2, cv2.INTER_LINEAR)

cv2.imshow("Remapped Image", remapped_img)
cv2.waitKey(0)
cv2.destroyAllWindows()

