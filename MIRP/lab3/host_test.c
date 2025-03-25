#define CL_USE_DEPRECATED_OPENCL_1_2_APIS
#define MAX_VAL 10000.0
#define MIN_VAL -10000.0

#include <stdio.h>
#include <stdlib.h>
#include <float.h>
#include <time.h>
#include <CL/cl.h>

// Функция для обработки ошибок
char errorHandling(const char* error_msg);

// Функция для загрузки ядра из файла
char* readKernelSource(const char* filename);

// Функция для генерации случайной матрицы
void generate_matrix(double* matrix, int rows, int cols, double min_val, double max_val);

// Функция для вывода матрицы
void print_matrix(double* matrix, int rows, int cols, const char* title);

// Функция для вычисления интегрального изображения
void compute_prefix_sum(double* matrix, double* prefix, int rows, int cols);


int main() {
    int rows, cols;
    printf("Enter the dimensions of the matrix (rows columns): ");
    scanf("%d %d", &rows, &cols);

    int sub_rows, sub_cols;
    printf("Enter the dimensions of the submatrix (rows columns): ");
    scanf("%d %d", &sub_rows, &sub_cols);

    // Проверка размеров подматрицы
    if (sub_rows > rows || sub_cols > cols) return errorHandling("Invalid submatrix size!");

    // Выделение памяти для матрицы и интегрального изображения
    double* matrix = (double*)malloc(rows * cols * sizeof(double));
    double* prefix = (double*)malloc((rows + 1) * (cols + 1) * sizeof(double));

    // Генерация матрицы
    generate_matrix(matrix, rows, cols, MIN_VAL, MAX_VAL);

    // Вычисление интегрального изображения
    compute_prefix_sum(matrix, prefix, rows, cols);

    // Инициализация OpenCL
    cl_platform_id platform;
    cl_device_id device;
    cl_context context;
    cl_command_queue queue;
    cl_program program;
    cl_kernel kernel;
    cl_event kernelEvent;

    cl_int err;

    // Получение платформы и устройства
    err = clGetPlatformIDs(1, &platform, NULL);
    if (err != CL_SUCCESS)  return errorHandling("Failed to get platform\n");

    err = clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, NULL);
    if (err != CL_SUCCESS)  return errorHandling("Failed to get device\n");

    // Создание контекста и очереди команд
    context = clCreateContext(NULL, 1, &device, NULL, NULL, &err);
    if (err != CL_SUCCESS)  return errorHandling("Failed to create context\n");

    queue = clCreateCommandQueue(context, device, CL_QUEUE_PROFILING_ENABLE, &err);
    // queue = clCreateCommandQueue(context, device, 0, &err);
    if (err != CL_SUCCESS)  return errorHandling("Failed to create command queue\n");

    // Загрузка ядра
    char* kernelSource = readKernelSource("kernel.cl");
    if (!kernelSource)  return errorHandling("Failed to read kernel source\n");

    program = clCreateProgramWithSource(context, 1, (const char**)&kernelSource, NULL, &err);
    if (err != CL_SUCCESS)  return errorHandling("Failed to create program\n");

    free(kernelSource);

    // Сборка ядра
    err = clBuildProgram(program, 0, NULL, NULL, NULL, NULL);
    if (err != CL_SUCCESS)  return errorHandling("Failed to build program\n");

    kernel = clCreateKernel(program, "find_max_submatrix", &err);
    if (err != CL_SUCCESS)  return errorHandling("Failed to create kernel\n");

    // Создание буферов
    cl_mem matrixBuffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, rows * cols * sizeof(double), matrix, &err);
    if (err != CL_SUCCESS)  return errorHandling("Failed to create matrix buffer\n");

    cl_mem prefixBuffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, (rows + 1) * (cols + 1) * sizeof(double), prefix, &err);
    if (err != CL_SUCCESS)  return errorHandling("Failed to create prefix buffer\n");

    // cl_mem resultBuffer = clCreateBuffer(context, CL_MEM_WRITE_ONLY, sizeof(double) + sizeof(int) * 2, NULL, &err);
    cl_mem resultBuffer = clCreateBuffer(context, CL_MEM_READ_WRITE, sizeof(double) + sizeof(int) * 2, NULL, &err);
    if (err != CL_SUCCESS)  return errorHandling("Failed to create result buffer\n");

    // Установка аргументов ядра
    err = clSetKernelArg(kernel, 0, sizeof(cl_mem), &matrixBuffer);
    if (err != CL_SUCCESS)  return errorHandling("Failed to set kernel arg 0\n");

    err = clSetKernelArg(kernel, 1, sizeof(cl_mem), &prefixBuffer);
    if (err != CL_SUCCESS)  return errorHandling("Failed to set kernel arg 1\n");

    err = clSetKernelArg(kernel, 2, sizeof(int), &rows);
    if (err != CL_SUCCESS)  return errorHandling("Failed to set kernel arg 2\n");

    err = clSetKernelArg(kernel, 3, sizeof(int), &cols);
    if (err != CL_SUCCESS)  return errorHandling("Failed to set kernel arg 3\n");

    err = clSetKernelArg(kernel, 4, sizeof(int), &sub_rows);
    if (err != CL_SUCCESS)  return errorHandling("Failed to set kernel arg 4\n");

    err = clSetKernelArg(kernel, 5, sizeof(int), &sub_cols);
    if (err != CL_SUCCESS)  return errorHandling("Failed to set kernel arg 5\n");

    err = clSetKernelArg(kernel, 6, sizeof(cl_mem), &resultBuffer);
    if (err != CL_SUCCESS)  return errorHandling("Failed to set kernel arg 6\n");

    // Запуск ядра
    size_t globalSize = (rows - sub_rows + 1) * (cols - sub_cols + 1);
    clock_t gpu_start = clock();
    err = clEnqueueNDRangeKernel(queue, kernel, 1, NULL, &globalSize, NULL, 0, NULL, NULL);
    if (err != CL_SUCCESS)  return errorHandling("Failed to enqueue kernel\n");
    clock_t gpu_end = clock();

    // Чтение результатов
    double max_sum;
    int max_i, max_j;
    err = clEnqueueReadBuffer(queue, resultBuffer, CL_TRUE, 0, sizeof(double) + sizeof(int) * 2, &max_sum, 0, NULL, NULL);
    if (err != CL_SUCCESS)  return errorHandling("Failed to read result buffer\n");

    // Временное решение: использовать результаты с CPU для позиции
    clock_t cpu_start = clock();
    double cpu_max_sum = -DBL_MAX;
    int cpu_max_i = 0, cpu_max_j = 0;
    for (int i = 0; i <= rows - sub_rows; i++) {
        for (int j = 0; j <= cols - sub_cols; j++) {
            double sum = prefix[(i + sub_rows) * (cols + 1) + j + sub_cols]
                       - prefix[i * (cols + 1) + j + sub_cols]
                       - prefix[(i + sub_rows) * (cols + 1) + j]
                       + prefix[i * (cols + 1) + j];

            if (sum > cpu_max_sum) {
                cpu_max_sum = sum;
                cpu_max_i = i;
                cpu_max_j = j;
            }
        }
    }
    clock_t cpu_end = clock();

    // Вывод результатов
    printf("\nResults:");
    printf("\nMaximum amount: %.2f", max_sum);
    printf("\nSub-matrix position: (%d, %d)", cpu_max_i, cpu_max_j); // Используем результаты с CPU для позиции

    // Время работы
    printf("\nCPU Execution time: %.6f sec", (double)(cpu_end - cpu_start) / CLOCKS_PER_SEC);
    printf("\nGPU Execution time: %.6f sec\n", (double)(gpu_end - gpu_start) / CLOCKS_PER_SEC);

    // Освобождение памяти
    clReleaseMemObject(matrixBuffer);
    clReleaseMemObject(prefixBuffer);
    clReleaseMemObject(resultBuffer);
    clReleaseProgram(program);
    clReleaseKernel(kernel);
    clReleaseCommandQueue(queue);
    clReleaseContext(context);

    free(matrix);
    free(prefix);

    system("pause");
    return 0;
}

char errorHandling(const char* error_msg){
    printf("%s\n", error_msg);
    system("pause");
    return 1;
}

// Функция для загрузки ядра из файла
char* readKernelSource(const char* filename) {
    FILE* file = fopen(filename, "rb");
    if (!file) return NULL;

    fseek(file, 0, SEEK_END);
    size_t size = ftell(file);
    rewind(file);

    char* buffer = (char*)malloc(size + 1);
    fread(buffer, size, 1, file);
    buffer[size] = '\0';

    fclose(file);
    return buffer;
}

// Функция для генерации случайной матрицы
void generate_matrix(double* matrix, int rows, int cols, double min_val, double max_val) {
    srand((unsigned int)time(NULL));

    for (int i = 0; i < rows * cols; i++) {
        double random = ((double)rand() / RAND_MAX);
        matrix[i] = min_val + random * (max_val - min_val);
    }
}

// Функция для вывода матрицы
void print_matrix(double* matrix, int rows, int cols, const char* title) {
    printf("\n%s (%dx%d):\n", title, rows, cols);
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            printf("%8.2f ", matrix[i * cols + j]);
        }
        printf("\n");
    }
}

// Функция для вычисления интегрального изображения
void compute_prefix_sum(double* matrix, double* prefix, int rows, int cols) {
    for (int i = 0; i <= rows; i++) {
        prefix[i * (cols + 1)] = 0;
    }
    for (int j = 0; j <= cols; j++) {
        prefix[j] = 0;
    }

    for (int i = 1; i <= rows; i++) {
        for (int j = 1; j <= cols; j++) {
            prefix[i * (cols + 1) + j] = matrix[(i - 1) * cols + j - 1] + prefix[(i - 1) * (cols + 1) + j] + prefix[i * (cols + 1) + j - 1] - prefix[(i - 1) * (cols + 1) + j - 1];
        }
    }
}
