#include <stdio.h>
#include <time.h>
#include <stdint.h>


// int _1sumDigits(int x); //  вычисляет сумму цифр числа
// char _1isValid(int x); // проверяет, равно ли число сумме своих цифр, возведённой в степень больше 1
// int _1findMinSequential(int N); // перебирает числа, начиная с N+1, пока не найдёт подходящее

// signed char _1consistentMain();


uint32_t sumDigits(uint32_t n); //  вычисляет сумму цифр числа


int satisfies(uint32_t n, uint32_t *digitSum, int *exp);

char consistentMain();