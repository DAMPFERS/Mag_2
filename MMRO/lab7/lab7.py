'''
import cv2
# Загрузка изображения
img = cv2.imread('2.jpg')
# Создание объекта детектора SIFT

sift = cv2.SIFT_create()
# Нахождение особых точек и описаний признаков на изображении
keypoints, descriptors = sift.detectAndCompute(img, None)
# Отмечаем найденные особые точки на исходном изображении
img_keypoints = cv2.drawKeypoints(img, keypoints, None)
# Выводим изображение с отмеченными особыми точками
cv2.imshow("Image with keypoints", img_keypoints)
# Вывод количества найденных особых точек
print("Found %d keypoints" % len(keypoints))
cv2.waitKey(0)
cv2.destroyAllWindows()
'''


import cv2
# Загрузка изображения
img = cv2.imread('2.jpg')
# Создание объекта детектора ORB
orb = cv2.ORB_create()
# Нахождение особых точек и описаний признаков на изображении
keypoints, descriptors = orb.detectAndCompute(img, None)
# Отмечаем найденные особые точки на исходном изображении
img_keypoints = cv2.drawKeypoints(img, keypoints, None)
# Выводим изображение с отмеченными особыми точками
cv2.imshow("Image with keypoints", img_keypoints)
# Вывод количества найденных особых точек
print("Found %d keypoints" % len(keypoints))
cv2.waitKey(0)
cv2.destroyAllWindows()
