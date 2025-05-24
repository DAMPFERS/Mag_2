
import cv2
import numpy as np

# Чтение левого и правого изображений.
imgL = cv2.imread("im2.jpg",0)
imgR = cv2.imread("im3.jpg",0)
# Настройка параметров для алгоритма StereoSGBM
minDisparity = 0;
numDisparities = 64;
blockSize = 8;
disp12MaxDiff = 1;
uniquenessRatio = 10;
speckleWindowSize = 10;
speckleRange = 8;
# Создание объекта алгоритма StereoSGBM
stereo = cv2.StereoSGBM_create(minDisparity = minDisparity,
numDisparities = numDisparities,
blockSize = blockSize,
disp12MaxDiff = disp12MaxDiff,
uniquenessRatio = uniquenessRatio,
speckleWindowSize = speckleWindowSize,
speckleRange = speckleRange
)
# Вычисление диспарита с использованием алгоритма StereoSGBM
disp = stereo.compute(imgL, imgR).astype(np.float32)
disp = cv2.normalize(disp,0,255,cv2.NORM_MINMAX)
# Отображение карты диспаратности
cv2.imshow("disparity",disp)
cv2.waitKey(0)
