// gcc main.c -o test.exe -I"C:\Program Files (x86)\Microsoft SDKs\MPI\Include" -L"C:\Program Files (x86)\Microsoft SDKs\MPI\Lib\x64" -lmsmpi

// mpiexec -n 4 test.exe

#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>

int main(void){
    
    MPI_Init(NULL, NULL); // start
    int rank, size;
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    printf("rank: %d, size %d", rank, size);
    MPI_Finalize(); // finish

    // system("pause");
    return 0;
}