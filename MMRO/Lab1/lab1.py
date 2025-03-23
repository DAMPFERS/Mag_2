import cv2

src = cv2.imread('pain.png')

cv2.rectangle(src, (30, 30), (300, 200), (0, 255, 0), 5)

cv2.imshow('Rectangle on Image', src)
cv2.waitKey(0)
cv2.destroyAllWindows()
