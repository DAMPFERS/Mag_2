#include "openmp_prog.h"

int sum_digits(int x) {
    int sum = 0;
    while (x != 0) {
        sum += x % 10;
        x /= 10;
    }
    return sum;
}


uint32_t findNumber_1(uint32_t n, int *exp){
    uint32_t s = sum_digits(n);
    // Если сумма цифр равна 0 или 1, то возведение в любую степень не даст n (исключаем)
    if (s <= 1)
        return 0;
    
    uint32_t power = s * s ; // начинаем с p = 2
    int p = 2;

    // Пока значение степени не превысило n
    while (power <= n) {
        power *= s;
        p++;
    }

    *exp = p;
    
    return power;
}


char is_valid(int x, int *power) {
    int s = sum_digits(x);
    if (s < 2) return 0;
    
    long current = s;
    current *= s; // s^2
    *power = 2;
    
    while (current <= x) {
        if (current == x) return 1;
        if (current > x / s) break; // Защита от переполнения
        
        current *= s;
        (*power)++;
    }
    return 0;
}



int find_min_parallel(int N, int chunk_size, int *global_power) {
    int result = -1;
    int found = 0;
    int current = N + 1;

    #pragma omp parallel
    {
        while (1) {
            int local_current, local_max;
            #pragma omp critical
            {
                if (found || current > 1000000000) {
                    local_current = -1;
                } else {
                    local_current = current;
                    current += chunk_size;
                    local_max = local_current + chunk_size;
                }
            }
            if (local_current == -1) break;

            for (int x = local_current; x < local_max; x++) {
                if (found) break;
                int local_power;
                if (is_valid(x, &local_power)) {
                    #pragma omp critical
                    {
                        if (!found || x < result) {
                            result = x;
                            *global_power = local_power;
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
    int power;
    int result = find_min_parallel(N, chunk_size, &power);
    double end = omp_get_wtime();

    printf("Minimum number: %d = (%d)^%d\n", result, sum_digits(N) ,power);
    printf("Time: %.6f sec\n", end - start);

    return 0;
}