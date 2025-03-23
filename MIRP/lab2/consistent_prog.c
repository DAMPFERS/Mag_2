#include "consistent_prog.h"
/*
int _1sumDigits(int x) { //  вычисляет сумму цифр числа
    int sum = 0;
    while (x != 0) {
        sum += x % 10;
        x /= 10;
    }
    return sum;
}


char _1isValid(int x) { // проверяет, равно ли число сумме своих цифр, возведённой в степень больше 1
    int s = sum_digits(x);
    if (s < 2) {
        return 0;
    }
    long current = s;
    current *= s; // s^2
    while (current <= x) {
        if (current == x) {
            return 1;
        }
        if (current > x / s) { // Предотвращение переполнения
            break;
        }
        current *= s;
    }
    return 0;
}


int _1findMinSequential(int N) {
    for (int x = N + 1; ; x++) {
        if (is_valid(x)) {
            return x;
        }
    }
}



signed char _1consistentMain(){
    int N;
    printf("Enter N: ");
    scanf("%d", &N);

    clock_t start = clock();
    int result = find_min_sequential(N);
    clock_t end = clock();
    double time = (double)(end - start) / CLOCKS_PER_SEC;

    printf("Minimum number: %d\n", result);
    printf("Execution time: %.6f seconds\n", time);

    return 0;
}

*/


// Функция для вычисления суммы десятичных цифр числа
uint32_t sumDigits(uint32_t n) {
    uint32_t sum = 0;
    while (n > 0) {
        sum += n % 10;
        n /= 10;
    }
    return sum;
}


// Функция проверяет, можно ли представить число n в виде (сумма цифр)^p, где p >= 2.
// Если да, возвращает 1 и записывает сумму цифр и степень в соответствующие переменные.
// Если нет – возвращает 0.
int satisfies(uint32_t n, uint32_t *digitSum, int *exp) {
    uint32_t s = sumDigits(n);
    // Если сумма цифр равна 0 или 1, то возведение в любую степень не даст n (исключаем)
    if (s <= 1)
        return 0;
    
    uint32_t power = s * s; // начинаем с p = 2
    int p = 2;
    // Пока значение степени не превысило n
    while (power <= n) {
        if (power == n) {
            *digitSum = s;
            *exp = p;
            return 1;
        }
        // Следующее возведение: умножаем текущий результат на s
        power *= s;
        p++;
    }
    return 0;
}


char consistentMain(){
    uint32_t N;
    printf("Enter a number N: ");
    scanf("%ld", &N);
    
    uint32_t candidate = N + 1; // начинаем поиск с N+1
    uint32_t s;
    int p;
    
    // Последовательный перебор кандидатов до нахождения нужного числа
    clock_t start = clock();
    while (1) {
        if (satisfies(candidate, &s, &p)) {
            clock_t end = clock();
            double time = (double)(end - start) / CLOCKS_PER_SEC;
            printf("Time:  %.6f seconds\n", time);
            printf("The number found: %ld = (%ld)^%d\n", candidate, s, p);
            break;
        }
        candidate++;
    }
    return 0;
}