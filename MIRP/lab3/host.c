/* Разуваве, АПИМ-24, Лабораторная работа №3, Разработка программы с использованием OpenMP
    Вариант 46
    Задача: Найти компактную подматрицу заданного размера с максимальной суммой элементов в заданной матрице 
    (компактной называется подматрица, в которой индексы соседних элементов по строкам и по столбцам различа-ются на единицу)
*/



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
    // Ввод параметров матрицы
    int rows, cols;
    printf("Enter matrix dimensions (rows columns): ");
    scanf("%d %d", &rows, &cols);

    int sub_rows, sub_cols;
    printf("Enter submatrix dimensions (rows columns): ");
    scanf("%d %d", &sub_rows, &sub_cols);

    // Проверка размеров подматрицы
    if (sub_rows > rows || sub_cols > cols)  return errorHandling("Invalid submatrix dimensions!");
    

    // Выделение памяти
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
    if (err != CL_SUCCESS) return errorHandling("Failed to get platform");

    err = clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, NULL);
    if (err != CL_SUCCESS) return errorHandling("Failed to get device");

    // Создание контекста и очереди с профилированием
    context = clCreateContext(NULL, 1, &device, NULL, NULL, &err);
    if (err != CL_SUCCESS) return errorHandling("Failed to create context");

    queue = clCreateCommandQueue(context, device, CL_QUEUE_PROFILING_ENABLE, &err);
    if (err != CL_SUCCESS) return errorHandling("Failed to create command queue");

    // Загрузка и компиляция ядра
    char* kernelSource = readKernelSource("kernel.cl");
    if (!kernelSource) return errorHandling("Failed to load kernel");

    program = clCreateProgramWithSource(context, 1, (const char**)&kernelSource, NULL, &err);
    free(kernelSource);
    if (err != CL_SUCCESS) return errorHandling("Failed to create program");

    err = clBuildProgram(program, 1, &device, NULL, NULL, NULL);
    if (err != CL_SUCCESS) {
        size_t log_size;
        clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, 0, NULL, &log_size);
        char* log = (char*)malloc(log_size);
        clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, log_size, log, NULL);
        printf("Build error:\n%s\n", log);
        free(log);
        return errorHandling("Failed to build program");
    }

    kernel = clCreateKernel(program, "find_max_submatrix", &err);
    if (err != CL_SUCCESS) return errorHandling("Failed to create kernel");

    // Создание буферов
    cl_mem matrixBuffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, 
                                        rows * cols * sizeof(double), matrix, &err);
    if (err != CL_SUCCESS) return errorHandling("Failed to create matrix buffer");

    cl_mem prefixBuffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, 
                                       (rows + 1) * (cols + 1) * sizeof(double), prefix, &err);
    if (err != CL_SUCCESS) return errorHandling("Failed to create prefix buffer");

    cl_mem resultSum = clCreateBuffer(context, CL_MEM_READ_WRITE, sizeof(double), NULL, &err);
    cl_mem resultI = clCreateBuffer(context, CL_MEM_READ_WRITE, sizeof(int), NULL, &err);
    cl_mem resultJ = clCreateBuffer(context, CL_MEM_READ_WRITE, sizeof(int), NULL, &err);
    if (err != CL_SUCCESS) return errorHandling("Failed to create result buffers");

    // Инициализация буферов результата
    double initSum = -DBL_MAX;
    int initI = -1, initJ = -1;
    err = clEnqueueWriteBuffer(queue, resultSum, CL_TRUE, 0, sizeof(double), &initSum, 0, NULL, NULL);
    err |= clEnqueueWriteBuffer(queue, resultI, CL_TRUE, 0, sizeof(int), &initI, 0, NULL, NULL);
    err |= clEnqueueWriteBuffer(queue, resultJ, CL_TRUE, 0, sizeof(int), &initJ, 0, NULL, NULL);
    if (err != CL_SUCCESS) return errorHandling("Failed to initialize result buffers");

    // Установка аргументов ядра
    err = 0;
    err |= clSetKernelArg(kernel, 0, sizeof(cl_mem), &matrixBuffer);
    err |= clSetKernelArg(kernel, 1, sizeof(cl_mem), &prefixBuffer);
    err |= clSetKernelArg(kernel, 2, sizeof(int), &rows);
    err |= clSetKernelArg(kernel, 3, sizeof(int), &cols);
    err |= clSetKernelArg(kernel, 4, sizeof(int), &sub_rows);
    err |= clSetKernelArg(kernel, 5, sizeof(int), &sub_cols);
    err |= clSetKernelArg(kernel, 6, sizeof(cl_mem), &resultSum);
    err |= clSetKernelArg(kernel, 7, sizeof(cl_mem), &resultI);
    err |= clSetKernelArg(kernel, 8, sizeof(cl_mem), &resultJ);
    if (err != CL_SUCCESS) return errorHandling("Failed to set kernel arguments");

    // Запуск ядра
    size_t globalSize = (rows - sub_rows + 1) * (cols - sub_cols + 1);
    err = clEnqueueNDRangeKernel(queue, kernel, 1, NULL, &globalSize, NULL, 0, NULL, &kernelEvent);
    if (err != CL_SUCCESS) return errorHandling("Failed to enqueue kernel");

    // Ожидание завершения и замер времени
    clFinish(queue);
    cl_ulong timeStart, timeEnd;
    clGetEventProfilingInfo(kernelEvent, CL_PROFILING_COMMAND_START, sizeof(timeStart), &timeStart, NULL);
    clGetEventProfilingInfo(kernelEvent, CL_PROFILING_COMMAND_END, sizeof(timeEnd), &timeEnd, NULL);
    double gpuTime = (timeEnd - timeStart) * 1e-9;

    // Чтение результатов
    double maxSum;
    int maxI, maxJ;
    err = clEnqueueReadBuffer(queue, resultSum, CL_TRUE, 0, sizeof(double), &maxSum, 0, NULL, NULL);
    err |= clEnqueueReadBuffer(queue, resultI, CL_TRUE, 0, sizeof(int), &maxI, 0, NULL, NULL);
    err |= clEnqueueReadBuffer(queue, resultJ, CL_TRUE, 0, sizeof(int), &maxJ, 0, NULL, NULL);
    if (err != CL_SUCCESS) return errorHandling("Failed to read results");

    // CPU вычисления для проверки
    clock_t cpuStart = clock();
    double cpuMax = -DBL_MAX;
    int cpuI = -1, cpuJ = -1;
    for (int i = 0; i <= rows - sub_rows; i++) {
        for (int j = 0; j <= cols - sub_cols; j++) {
            double sum = prefix[(i + sub_rows) * (cols + 1) + j + sub_cols]
                       - prefix[i * (cols + 1) + j + sub_cols]
                       - prefix[(i + sub_rows) * (cols + 1) + j]
                       + prefix[i * (cols + 1) + j];
            if (sum > cpuMax) {
                cpuMax = sum;
                cpuI = i;
                cpuJ = j;
            }
        }
    }
    double cpuTime = (double)(clock() - cpuStart) / CLOCKS_PER_SEC;

    // Вывод результатов
    printf("\nGPU Results:");
    printf("\nMax sum: %.2f", maxSum);
    printf("\nPosition: (%d, %d)", maxI, maxJ);
    printf("\nGPU Time: %.6f sec", gpuTime);

    printf("\n\nCPU Results:");
    printf("\nMax sum: %.2f", cpuMax);
    printf("\nPosition: (%d, %d)", cpuI, cpuJ);
    printf("\nCPU Time: %.6f sec\n", cpuTime);

    // Освобождение ресурсов
    clReleaseMemObject(matrixBuffer);
    clReleaseMemObject(prefixBuffer);
    clReleaseMemObject(resultSum);
    clReleaseMemObject(resultI);
    clReleaseMemObject(resultJ);
    clReleaseProgram(program);
    clReleaseKernel(kernel);
    clReleaseCommandQueue(queue);
    clReleaseContext(context);
    free(matrix);
    free(prefix);

    system("pause");
    return 0;
}


char errorHandling(const char* error_msg) {
    fprintf(stderr, "Error: %s\n", error_msg);
    system("pause");
    exit(EXIT_FAILURE);
}

char* readKernelSource(const char* filename) {
    FILE* fp = fopen(filename, "r");
    if (!fp) return NULL;

    fseek(fp, 0, SEEK_END);
    long size = ftell(fp);
    rewind(fp);

    char* source = (char*)malloc(size + 1);
    fread(source, 1, size, fp);
    source[size] = '\0';

    fclose(fp);
    return source;
}

void generate_matrix(double* matrix, int rows, int cols, double min_val, double max_val) {
    srand(time(NULL));
    for (int i = 0; i < rows * cols; i++) {
        matrix[i] = min_val + (max_val - min_val) * ((double)rand() / RAND_MAX);
    }
}

void compute_prefix_sum(double* matrix, double* prefix, int rows, int cols) {
    for (int i = 0; i <= rows; i++) prefix[i * (cols + 1)] = 0;
    for (int j = 0; j <= cols; j++) prefix[j] = 0;

    for (int i = 1; i <= rows; i++) {
        for (int j = 1; j <= cols; j++) {
            prefix[i*(cols+1)+j] = matrix[(i-1)*cols + (j-1)] 
                                  + prefix[(i-1)*(cols+1)+j] 
                                  + prefix[i*(cols+1)+(j-1)] 
                                  - prefix[(i-1)*(cols+1)+(j-1)];
        }
    }
}