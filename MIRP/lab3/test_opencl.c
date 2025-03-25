#define CL_TARGET_OPENCL_VERSION 220
#include <stdio.h>
#include <stdlib.h>
#include <CL/cl.h>

int main() {
    cl_uint platformCount;
    clGetPlatformIDs(0, NULL, &platformCount);

    if (platformCount == 0) {
        printf("OpenCL not found!\n");
        return 1;
    }

    printf("OpenCL platforms found: %u\n", platformCount);
    system("pause");
    return 0;
}
