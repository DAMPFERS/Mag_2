/* Разуваве, АПИМ-24, Лабораторная работа №2, Разработка программы с использованием OpenMP
    Вариант 46
    Задача: Найти компактную подматрицу заданного размера с максимальной суммой элементов в заданной матрице 
    (компактной называется подматрица, в которой индексы соседних элементов по строкам и по столбцам различа-ются на единицу)

    ПОСЛЕДОВАТЕЛЬНАЯ ПРОГРАММА
*/



#define MIN_VAL -10000.0
#define MAX_VAL 10000.0

#include <stdio.h>
#include <stdlib.h>
#include <float.h>
#include <time.h>

void generate_matrix(double **matrix, int rows, int cols, double min_val, double max_val);
void compute_prefix_sum(double **matrix, double **prefix, int rows, int cols);
void print_matrix(double **matrix, int rows, int cols, const char *title);

int main() {
    int rows, cols;
    printf("Enter the dimensions of the matrix (rows columns): ");
    scanf("%d %d", &rows, &cols);

    int sub_rows, sub_cols;
    printf("Enter the dimensions of the submatrix (rows columns): ");
    scanf("%d %d", &sub_rows, &sub_cols);

    // Генерация матрицы
    double **matrix = (double **)malloc(rows * sizeof(double *));
    for (int i = 0; i < rows; i++) {
        matrix[i] = (double *)malloc(cols * sizeof(double));
    }
    generate_matrix(matrix, rows, cols, MIN_VAL, MAX_VAL);
    // print_matrix(matrix, rows, cols, "Initial matrix");

    // Вычисление интегрального изображения
    double **prefix = (double **)malloc((rows+1) * sizeof(double *));
    for (int i = 0; i <= rows; i++) {
        prefix[i] = (double *)malloc((cols+1) * sizeof(double));
    }
    compute_prefix_sum(matrix, prefix, rows, cols);

    // Поиск подматрицы
    clock_t start = clock();
    double max_sum = -DBL_MAX;
    int max_i = 0, max_j = 0;

    for (int i = 0; i <= rows - sub_rows; i++) {
        for (int j = 0; j <= cols - sub_cols; j++) {
            double sum = prefix[i+sub_rows][j+sub_cols] 
                        - prefix[i][j+sub_cols] 
                        - prefix[i+sub_rows][j] 
                        + prefix[i][j];
            if (sum > max_sum) {
                max_sum = sum;
                max_i = i;
                max_j = j;
            }
        }
    }
    clock_t end = clock();
    double time_spent = (double)(end - start); /// CLOCKS_PER_SEC;

    // Вывод результатов
    printf("\nResults:");
    printf("\nMaximum amount: %.2f", max_sum);
    printf("\nSub-matrix position: (%d, %d)", max_i, max_j);
    printf("\nExecution time: %.6f msec", time_spent);

    // Вывод подматрицы
    double **submatrix = (double **)malloc(sub_rows * sizeof(double *));
    for (int i = 0; i < sub_rows; i++) {
        submatrix[i] = (double *)malloc(sub_cols * sizeof(double));
        for (int j = 0; j < sub_cols; j++) {
            submatrix[i][j] = matrix[max_i + i][max_j + j];
        }
    }
    print_matrix(submatrix, sub_rows, sub_cols, "\nFound submatrix");

    // Освобождение памяти
    for (int i = 0; i < rows; i++) free(matrix[i]);
    free(matrix);
    for (int i = 0; i <= rows; i++) free(prefix[i]);
    free(prefix);
    system("pause");
    return 0;
}

void generate_matrix(double **matrix, int rows, int cols, double min_val, double max_val) {
    srand((unsigned int)time(NULL));
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            double random = ((double)rand() / RAND_MAX); 
            matrix[i][j] = min_val + random * (max_val - min_val);
        }
    }
}

void print_matrix(double **matrix, int rows, int cols, const char *title) {
    printf("\n%s (%dx%d):\n", title, rows, cols);
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            printf("%8.2f ", matrix[i][j]);
        }
        printf("\n");
    }
}

void compute_prefix_sum(double **matrix, double **prefix, int rows, int cols) {
    for (int i = 0; i <= rows; i++) prefix[i][0] = 0;
    for (int j = 0; j <= cols; j++) prefix[0][j] = 0;

    for (int i = 1; i <= rows; i++) {
        for (int j = 1; j <= cols; j++) {
            prefix[i][j] = matrix[i-1][j-1] 
                          + prefix[i-1][j] 
                          + prefix[i][j-1] 
                          - prefix[i-1][j-1];
        }
    }
}
