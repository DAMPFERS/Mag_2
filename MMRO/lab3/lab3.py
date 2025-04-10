# import matplotlib as mpl
import matplotlib.pyplot as plt
import numpy as np
import numpy.linalg as la
import statsmodels.api as sm
# mpl.use("MacOSX")


class Noise:
    
    @staticmethod
    
    def make_noise(y, p: float, law="uniform"):
        """
        p - уровень шума, от 0 до 1
        """
        eps = abs(y * p)
        if law == "uniform":
            return np.random.uniform(y - eps, y + eps)
        elif law == "normal":
            return np.random.normal(y, eps / 3)
        
        
def f1(x, *args) -> float:
    return np.sin(x**2) - x


def f2(x, *args) -> float:
    return 2 * np.sin(10*x) * np.exp(-0.1 * x**2) 

def f3(x, *args) -> float:
    return np.log(x+0.1)
    

def tls(x, y):
    if x.ndim == 1:
        n = 1 # the number of variable of x
        x = x.reshape(len(x), 1)
    else:
        n = np.array(x).shape[1]
        
    Z = np.vstack((x.T, y)).T
    U, s, Vt = la.svd(Z, full_matrices=True)
    V = Vt.T
    Vxy = V[:n, n:]
    Vyy = V[n:, n:]
    a_tls = - Vxy / Vyy # total least squares soln
    xtyt = - Z.dot(V[:, n:]).dot(V[:, n:].T)
    xt = xtyt[:, :n] # x error
    y_tls = (x + xt).dot(a_tls)
    fro_norm = la.norm(xtyt, 'fro')
    return y_tls, x + xt, a_tls, fro_norm


func_s = [f1, f2, f3]
# func_s = [f3]
p = 0.10 # процент шума, от 0 до 1
N = 30 # количество точек


for i in range(len(func_s)):
    x_min, x_max = 0, 10
    X = np.linspace(x_min, x_max, N)
    print(f"Функция {i+1}")
    y = func_s[i](X)
    print(X)
    print(y)

# noising
    x_noised = Noise.make_noise(X, p)
    y_noised = Noise.make_noise(y, p)
    print(x_noised)
    print(y_noised)
##########################


    # OLS
    fit_ols = sm.OLS(y_noised, sm.add_constant(X)).fit()
    print(fit_ols.summary())
    print("Parameters: ", fit_ols.params)
    print("Standard errors: ", fit_ols.bse)
    print("R2: ", fit_ols.rsquared)
    ##########################


    # WLS
    weights = np.ones(N)
    weights[N * 6 // 10:] = 3
    weights = 1.0 / (weights ** 2)
    fit_wls = sm.WLS(y, X, weights=weights).fit()
    print(fit_wls.summary())
    print("Parameters: ", fit_wls.params)
    print("Standard errors: ", fit_wls.bse)
    print("R2: ", fit_wls.rsquared)
    ##########################
    # TLS
    y_tls, x_tls, a_tls, from_norm = tls(x_noised, y_noised)
    ##########################
    # PREPARE
    y_ols = fit_ols.fittedvalues
    y_wls = fit_wls.fittedvalues
    ##########################
    # STATS
    print("======================================")
    print("==============ERROR NORM==============")
    print("--------------------------------------")
    print(f' err_noised: {round(np.linalg.norm(y - y_noised), 5)}')
    print(f' err_ols: {round(np.linalg.norm(y - y_ols), 5)}')
    print(f' err_wls: {round(np.linalg.norm(y - y_wls), 5)}')
    print(f' err_tls: {round(np.linalg.norm(y - y_tls), 5)}')
    print(f' err_ols_noised: {round(np.linalg.norm(y_noised - y_ols), 5)}')
    print(f' err_wls_noised: {round(np.linalg.norm(y_noised - y_wls), 5)}')
    print(f' err_tls_noised: {round(np.linalg.norm(y_noised - y_tls), 5)}')
    print("--------------------------------------")
    print("\n\n")
    ##########################
    # plotting
    plt.plot(X, y, "-", label="original")
    plt.plot(X, fit_ols.fittedvalues, "--", label="OLS")
    plt.plot(X, fit_wls.fittedvalues, "--", label="WLS")
    plt.plot(x_tls, y_tls, "--", label="TLS")
    plt.plot(X, y_noised, "o", label="noised(y)", markersize=3)
    plt.plot(x_noised, y_noised, "o", label="noised(x,y)", markersize=3)
    plt.legend()
    plt.show()