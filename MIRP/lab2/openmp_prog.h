#include <stdio.h>
#include <omp.h>
#include <time.h>
#include <stdint.h>

// Функция для вычисления суммы десятичных цифр числа
int sum_digits(int x);

char is_valid(int x, int *power);

uint32_t findNumber_1(uint32_t n, int *exp);

int find_min_parallel(int N, int chunk_size, int *global_power); // Функция проверяет, можно ли представить число n в виде (сумма цифр)^p, где p >= 2.

char openMpMain();