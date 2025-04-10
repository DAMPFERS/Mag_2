import cv2
# Попробуем открыть камеру
cap = cv2.VideoCapture(0)
# Проверяем, удалось ли открыть камеру
if not cap.isOpened():
    print("Ошибка: не удалось открыть камеру.")
else:
    detector = cv2.QRCodeDetector()

    while True:
        ret, img = cap.read()

        # Проверяем, удалось ли получить кадр
        if not ret:
            print("Ошибка: не удалось получить кадр с камеры.")
            break

        data, vertices_array, _ = detector.detectAndDecode(img)

        if vertices_array is not None:
            if data:
                print("QR Code detected, data:", data)

        cv2.imshow("img", img)

        if cv2.waitKey(1) == ord("q"):
            break

    cap.release()
    cv2.destroyAllWindows()
