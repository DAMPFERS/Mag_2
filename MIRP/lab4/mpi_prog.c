#define MIN_VAL -10000.0
#define MAX_VAL 10000.0

#include <stdio.h>
#include <stdlib.h>
#include <float.h>
#include <time.h>
#include <mpi.h>

typedef struct {
    double sum;
    int i;
    int j;
} MaxLoc;

void generate_matrix(double **matrix, int rows, int cols, double min_val, double max_val);
void compute_prefix_sum(double **matrix, double *prefix, int rows, int cols);
void print_matrix(double **matrix, int rows, int cols, const char *title);
void maxloc_reduce(void *in, void *inout, int *len, MPI_Datatype *type);

int main(void) {
    
    MPI_Init(NULL, NULL);
    int rank, size;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    int rows, cols, sub_rows, sub_cols;
    double *prefix = NULL;
    double **matrix = NULL;
    MaxLoc global_max;
    MPI_Datatype MPI_MAXLOC_TYPE;
    MPI_Op MPI_MAXLOC_OP;

    // Создание пользовательского типа MPI_MAXLOC_TYPE
    int blocklengths[3] = {1, 1, 1};
    MPI_Datatype types[3] = {MPI_DOUBLE, MPI_INT, MPI_INT};
    MPI_Aint offsets[3];
    offsets[0] = offsetof(MaxLoc, sum);
    offsets[1] = offsetof(MaxLoc, i);
    offsets[2] = offsetof(MaxLoc, j);
    MPI_Type_create_struct(3, blocklengths, offsets, types, &MPI_MAXLOC_TYPE);
    MPI_Type_commit(&MPI_MAXLOC_TYPE);

    // Создание пользовательской операции MPI_MAXLOC_OP
    MPI_Op_create(maxloc_reduce, 1, &MPI_MAXLOC_OP);

    if (rank == 0) {
        printf("Enter the dimensions of the matrix (rows columns): ");
        scanf("%d %d", &rows, &cols);
        printf("Enter the dimensions of the submatrix (rows columns): ");
        scanf("%d %d", &sub_rows, &sub_cols);

        // Проверка допустимости размеров подматрицы
        if (sub_rows > rows || sub_cols > cols) {
            fprintf(stderr, "Error: Submatrix dimensions exceed matrix dimensions.\n");
            MPI_Abort(MPI_COMM_WORLD, 1);
        }

        // Генерация матрицы
        matrix = (double **)malloc(rows * sizeof(double *));
        for (int i = 0; i < rows; i++) {
            matrix[i] = (double *)malloc(cols * sizeof(double));
        }
        generate_matrix(matrix, rows, cols, MIN_VAL, MAX_VAL);

        // Вычисление интегрального изображения
        prefix = (double *)malloc((rows + 1) * (cols + 1) * sizeof(double));
        compute_prefix_sum(matrix, prefix, rows, cols);
    }

    // Рассылка размеров матрицы и подматрицы
    MPI_Bcast(&rows, 1, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Bcast(&cols, 1, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Bcast(&sub_rows, 1, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Bcast(&sub_cols, 1, MPI_INT, 0, MPI_COMM_WORLD);

    // Выделение памяти под интеграл в остальных процессах
    if (rank != 0) {
        prefix = (double *)malloc((rows + 1) * (cols + 1) * sizeof(double));
    }

    // Рассылка интегрального изображения
    MPI_Bcast(prefix, (rows + 1) * (cols + 1), MPI_DOUBLE, 0, MPI_COMM_WORLD);

    // Вычисление диапазонов для текущего процесса
    int total_i = rows - sub_rows + 1;
    int start_i = (total_i * rank) / size;
    int end_i = (total_i * (rank + 1)) / size;

    MaxLoc local_max = {-DBL_MAX, 0, 0};

    clock_t start = clock();
    for (int i = start_i; i < end_i; i++) {
        for (int j = 0; j <= cols - sub_cols; j++) {
            double sum = prefix[(i + sub_rows) * (cols + 1) + (j + sub_cols)]
                        - prefix[i * (cols + 1) + (j + sub_cols)]
                        - prefix[(i + sub_rows) * (cols + 1) + j]
                        + prefix[i * (cols + 1) + j];
            if (sum > local_max.sum) {
                local_max.sum = sum;
                local_max.i = i;
                local_max.j = j;
            }
        }
    }
    clock_t end = clock();
    double time_spent = (double)(end - start);

    // Сбор результатов
    MPI_Reduce(&local_max, &global_max, 1, MPI_MAXLOC_TYPE, MPI_MAXLOC_OP, 0, MPI_COMM_WORLD);

    if (rank == 0) {
        printf("\nResults:");
        printf("\nMaximum amount: %.2f", global_max.sum);
        printf("\nSub-matrix position: (%d, %d)", global_max.i, global_max.j);
        printf("\nExecution time: %.6f msec", time_spent);
        // system("pause");

        // Освобождение памяти
        for (int i = 0; i < rows; i++) free(matrix[i]);
        free(matrix);
        free(prefix);
    } else {
        free(prefix);
    }

    MPI_Type_free(&MPI_MAXLOC_TYPE);
    MPI_Op_free(&MPI_MAXLOC_OP);
    MPI_Finalize();

    

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

void compute_prefix_sum(double **matrix, double *prefix, int rows, int cols) {
    for (int i = 0; i <= rows; i++) prefix[i * (cols + 1) + 0] = 0;
    for (int j = 0; j <= cols; j++) prefix[0 * (cols + 1) + j] = 0;

    for (int i = 1; i <= rows; i++) {
        for (int j = 1; j <= cols; j++) {
            prefix[i * (cols + 1) + j] = matrix[i - 1][j - 1]
                + prefix[(i - 1) * (cols + 1) + j]
                + prefix[i * (cols + 1) + (j - 1)]
                - prefix[(i - 1) * (cols + 1) + (j - 1)];
        }
    }
}

void maxloc_reduce(void *in, void *inout, int *len, MPI_Datatype *type) {
    MaxLoc *invals = (MaxLoc *)in;
    MaxLoc *inoutvals = (MaxLoc *)inout;
    for (int i = 0; i < *len; i++) {
        if (invals[i].sum > inoutvals[i].sum) {
            inoutvals[i] = invals[i];
        }
    }
}