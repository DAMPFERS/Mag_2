#include "consistent_prog.h"

// Функция для вычисления суммы десятичных цифр числа
uint32_t sumDigits(__uint128_t n) {
    uint32_t sum = 0;
    while (n > 0) {
        sum += n % 10;
        n /= 10;
    }
    return sum;
}


__uint128_t findNumber(__uint128_t n, int *exp){
    uint32_t s = sumDigits(n);
    // Если сумма цифр равна 0 или 1, то возведение в любую степень не даст n (исключаем)
    if (s <= 1)
        return 0;
    
    __uint128_t power = s * s ; // начинаем с p = 2
    int p = 2;

    // Пока значение степени не превысило n
    while (power <= n) {
        power *= s;
        p++;
    }

    *exp = p;
    
    return power;
}



char consistentMain(){
    __uint128_t N;
    printf("Enter a number N: ");
    scanf("%I128u", &N);
    
    int p;
    
    // Последовательный перебор кандидатов до нахождения нужного числа
    clock_t start = clock();
    __uint128_t res = findNumber(N, &p);
    clock_t end = clock();

    double time = (double)(end - start) / CLOCKS_PER_SEC;
    printf("Time:  %.12f seconds\n", time);
    printf("The number found: %I64u = (%I64u)^%d\n", res, sumDigits(N), p);
    
    return 0;
}