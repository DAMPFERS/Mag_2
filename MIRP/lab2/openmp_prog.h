#include <stdio.h>
#include <omp.h>
#include <time.h>

// Функция для вычисления суммы десятичных цифр числа
// long sumDigits2(long n);
int sum_digits(int x);

char is_valid(int x);

int find_min_parallel(int N, int chunk_size);

// Функция проверяет, можно ли представить число n в виде (сумма цифр)^p, где p >= 2.
// Если да, возвращает 1 и записывает сумму цифр и степень в соответствующие переменные.
// int satisfies2(long n, long *digitSum, int *exp);

// char openMpMain2();
char openMpMain();