import numpy as np
import matplotlib.pyplot as plt
t = np.arange(0, 100, 0.1)
y = np.cos(t)
np.savetxt("sinusoid3.txt", y, fmt="%.3f")