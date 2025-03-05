#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <time.h>
#include <stdbool.h>

#define MAX_NODES 10            // Максимальное количество узлов (городов/остановок)
#define TRAVEL_TIME 1           // Время в секундах, которое автобус тратит на перемещение
#define GENERATION_INTERVAL 2   // Максимальное время в секундах между генерацией новых пассажиров


typedef struct Passenger{
    int id;
    int source;         // начальная остановка
    int destination;    // конечная остановка
    struct Passenger *next;     // указатель на следующего пассажира в списке
} Passenger;

typedef struct {
    Passenger *head;
    Passenger *tail;
} PassengerList;

typedef struct Node {
    int id;
    pthread_mutex_t mutex;  // мьютекс для синхронизации доступа
    pthread_cond_t cond;    // условная переменная для ожидания автобуса
    PassengerList passengers; // очередь пассажиров на остановке
    int neighbor_count;       // число соседних узлов
    int neighbors[MAX_NODES]; // массив идентификаторов соседей
} Node;

typedef struct Bus {
    int id;
    int capacity;
    PassengerList passengers;
    Node *current_node;
} Bus;



extern volatile Node nodes[MAX_NODES];
extern volatile int node_count; // количество узлов
extern volatile int bus_count;  // количество автобусов
extern volatile int passenger_id; // глобальный счётчик пассажиров
extern volatile pthread_mutex_t passenger_id_mutex; // мьютекс для безопасного увеличения passenger_id


void passenger_list_init(PassengerList *list);                                              // Инициализация связного списка для хранения списка пассажиров
void passenger_list_add(PassengerList *list, Passenger *p);                                 // Добавление пассажира  в конец списка
Passenger *passenger_list_remove(PassengerList *list, Passenger *prev, Passenger *current); // Удаление пассажира списка 
void *bus_thread(void *arg);                                                                // Основная функция потока автобуса (Выбор следующей остановки, Посадка пассажиров, Выезд автобуса, Высадка пассажиров)
void *passenger_generator(void *arg);                                                       // Генерация пассажиров
void load_graph(const char *filename);                                                      // Чтение графа из файла