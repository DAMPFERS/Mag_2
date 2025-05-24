#pragma OPENCL EXTENSION cl_khr_int64_base_atomics : enable

__kernel void find_max_submatrix(
    __global double* matrix,
    __global double* prefix,
    int rows,
    int cols,
    int sub_rows,
    int sub_cols,
    __global double* result_sum,
    __global int* result_i,
    __global int* result_j
) {
    int idx = get_global_id(0);
    int total = (rows - sub_rows + 1) * (cols - sub_cols + 1);
    if (idx >= total) return;

    int i = idx / (cols - sub_cols + 1);
    int j = idx % (cols - sub_cols + 1);

    double sum = prefix[(i + sub_rows) * (cols + 1) + j + sub_cols] 
               - prefix[i * (cols + 1) + j + sub_cols] 
               - prefix[(i + sub_rows) * (cols + 1) + j] 
               + prefix[i * (cols + 1) + j];

    long old_val, new_val;
    do {
        old_val = *((__global long*)result_sum);
        new_val = as_long(max(as_double(old_val), sum));
        if (as_double(old_val) >= sum) break;
    } while (atom_cmpxchg((__global long*)result_sum, old_val, new_val) != old_val);

    if (sum == as_double(*((__global long*)result_sum))) {
        atomic_xchg(result_i, i);
        atomic_xchg(result_j, j);
    }
}