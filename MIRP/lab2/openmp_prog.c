#include "openmp_prog.h"
/*
// Функция для вычисления суммы десятичных цифр числа
long sumDigits2(long n) {
    long sum = 0;
    while (n > 0) {
        sum += n % 10;
        n /= 10;
    }
    return sum;
}

// Функция проверяет, можно ли представить число n в виде (сумма цифр)^p, где p >= 2.
// Если да, возвращает 1 и записывает сумму цифр и степень в соответствующие переменные.
int satisfies2(long n, long *digitSum, int *exp) {
    long s = sumDigits(n);
    if (s <= 1)
        return 0;
    
    long power = s * s; // начинаем с p = 2
    int p = 2;
    while (power <= n) {
        if (power == n) {
            *digitSum = s;
            *exp = p;
            return 1;
        }
        power *= s;
        p++;
    }
    return 0;
}

char openMpMain2(){
    long N;
    printf("Enter a number N: ");
    scanf("%ld", &N);
    
    // Глобальный атомарный счётчик кандидатов
    long candidate = N + 1;
    // Переменные для хранения найденного результата
    long bestCandidate = 0;
    long bestSum = 0;
    int bestExp = 0;
    
    // Флаг, сигнализирующий о нахождении решения
    int found = 0;
    
    // Параллельная область OpenMP
    #pragma omp parallel shared(candidate, found, bestCandidate, bestSum, bestExp)
    {
        long localCandidate;
        long s;
        int p;
        
        // Каждый поток в цикле получает новое число для проверки, пока не найдено решение.
        while (!found) {
            // Атомарное получение следующего кандидата
            #pragma omp atomic capture
            localCandidate = candidate++;
            
            if (satisfies(localCandidate, &s, &p)) {
                // Критическая секция для обновления общих переменных
                #pragma omp critical
                {
                    if (!found || localCandidate < bestCandidate) {
                        bestCandidate = localCandidate;
                        bestSum = s;
                        bestExp = p;
                        found = 1; // устанавливаем флаг, что решение найдено
                    }
                }
            }
        }
    }
    
    printf("The number found: %ld = (%ld)^%d\n", bestCandidate, bestSum, bestExp);
    return 0;
}


*/



///////////////////////////////////////

int sum_digits(int x) {
    int sum = 0;
    while (x != 0) {
        sum += x % 10;
        x /= 10;
    }
    return sum;
}

char is_valid(int x) {
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

int find_min_parallel(int N, int chunk_size) {
    int result = -1;
    int found = 0;
    int current = N + 1;

    #pragma omp parallel
    {
        while (1) {
            int local_current, local_max;
            #pragma omp critical
            {
                if (found || current > 1000000000) { // Верхний предел для предотвращения бесконечного цикла
                    local_current = -1;
                } else {
                    local_current = current;
                    current += chunk_size;
                    local_max = local_current + chunk_size;
                }
            }
            if (local_current == -1) break;

            for (int x = local_current; x < local_max; x++) {
                if (found) break; // Досрочный выход, если решение найдено
                if (is_valid(x)) {
                    #pragma omp critical
                    {
                        if (!found || x < result) {
                            result = x;
                            found = 1;
                        }
                    }
                    break;
                }
            }
        }
    }

    return result;
}

char openMpMain(){
    int N, chunk_size, threads;
    printf("Enter N: ");
    scanf("%d", &N);
    printf("Enter the size of the chunk: ");
    scanf("%d", &chunk_size);
    printf("Enter the number of threads: ");
    scanf("%d", &threads);

    omp_set_num_threads(threads);

    double start = omp_get_wtime();
    int result = find_min_parallel(N, chunk_size);
    double end = omp_get_wtime();

    printf("Minimum number: %d\n", result);
    printf("Execution time: %.6f seconds\n", end - start);

    return 0;
}