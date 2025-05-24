import numpy as np
from tensorflow import keras
from keras.models import Sequential
from tensorflow.keras.layers import TimeDistributed, Dense, Dropout, SimpleRNN, RepeatVector, Input
from tensorflow.keras.callbacks import EarlyStopping, LambdaCallback
from termcolor import colored

# Глобальные параметры
all_chars = '0123456789+'
num_features = len(all_chars)
char_to_index = dict((c, i) for i, c in enumerate(all_chars))
index_to_char = dict((i, c) for i, c in enumerate(all_chars))
hidden_units = 128
max_time_steps = 5


# Функция для генерации данных
def generate_data():
    first_num = np.random.randint(low=0, high=100)
    second_num = np.random.randint(low=0, high=100)
    example = str(first_num) + '+' + str(second_num)
    label = str(first_num + second_num)
    return example, label


# Функции для векторного представления данных
def vectorize_example(example, label):
    x = np.zeros((max_time_steps, num_features))
    y = np.zeros((max_time_steps, num_features))

    diff_x = max_time_steps - len(example)
    diff_y = max_time_steps - len(label)

    for i, c in enumerate(example):
        x[diff_x + i, char_to_index[c]] = 1
    for i in range(diff_x):
        x[i, char_to_index['0']] = 1
    for i, c in enumerate(label):
        y[diff_y + i, char_to_index[c]] = 1
    for i in range(diff_y):
        y[i, char_to_index['0']] = 1

    return x, y


def devectorize_example(example):
    result = [index_to_char[np.argmax(vec)] for i, vec in enumerate(example)]
    return ''.join(result)


# Функция для создания датасета
def create_dataset(num_examples=2000):
    x_train = np.zeros((num_examples, max_time_steps, num_features))
    y_train = np.zeros((num_examples, max_time_steps, num_features))

    for i in range(num_examples):
        e, l = generate_data()
        x, y = vectorize_example(e, l)
        x_train[i] = x
        y_train[i] = y

    return x_train, y_train


# Функция для тестирования модели и вычисления ошибки
def test_model(model, num_test_examples=100):
    x_test, y_test = create_dataset(num_test_examples)
    preds = model.predict(x_test)

    error_count = 0

    for i, pred in enumerate(preds):
        y = devectorize_example(y_test[i])
        y_hat = devectorize_example(pred)
        col = 'green'
        if y != y_hat:
            col = 'red'
            error_count += 1

        out = 'Input: ' + devectorize_example(x_test[i]) + ' Out: ' + y + ' Pred: ' + y_hat
        print(colored(out, col))

    error_prob = error_count / num_test_examples
    print(f"\nКоличество ошибок: {error_count} из {num_test_examples}")
    print(f"Вероятность ошибки: {error_prob:.2%}")
    return error_prob


# Создание и обучение модели
def train_and_test(num_train_examples=2000, num_test_examples=100):
    # Создание модели
    model = Sequential([
        Input(shape=(None, num_features)),  # Явное указание входного слоя
        SimpleRNN(hidden_units),
        RepeatVector(max_time_steps),
        SimpleRNN(hidden_units, return_sequences=True),
        TimeDistributed(Dense(num_features, activation='softmax'))
    ])

    model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

    # Создание обучающего датасета
    x_train, y_train = create_dataset(num_train_examples)

    # Использование EarlyStopping для предотвращения переобучения
    early_stopping = EarlyStopping(monitor='val_loss', patience=5)

    # Обучение модели
    print(f"\nОбучение на {num_train_examples} примерах...")
    model.fit(
        x_train,
        y_train,
        epochs=500,
        batch_size=256,
        validation_split=0.2,
        verbose=1,  # Показать процесс обучения
        callbacks=[early_stopping]
    )

    # Тестирование модели
    print(f"\nТестирование на {num_test_examples} примерах:")
    error_prob = test_model(model, num_test_examples)

    return error_prob


# Проведение экспериментов
print("Эксперимент 1: Обучение на 2000 примерах")
error_2000 = train_and_test(2000, 100)

print("\nЭксперимент 2: Обучение на 5000 примерах")
error_5000 = train_and_test(5000, 100)

# Сравнение результатов
print("\nСравнение результатов:")
print(f"Ошибка при обучении на 2000 примерах: {error_2000:.2%}")
print(f"Ошибка при обучении на 5000 примерах: {error_5000:.2%}")
print(f"Улучшение: {error_2000 - error_5000:.2%}")