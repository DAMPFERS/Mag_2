// #include <stdio.h>
// #include <stdlib.h>
// #include <pthread.h>
// #include <unistd.h>
// #include <time.h>
// #include <stdbool.h>

// #define MAX_NODES 10            // Максимальное количество узлов (городов/остановок)
// #define TRAVEL_TIME 1           // Время в секундах, которое автобус тратит на перемещение
// #define GENERATION_INTERVAL 2   // Максимальное время в секундах между генерацией новых пассажиров


// typedef struct Passenger{
//     int id;
//     int source;         // начальная остановка
//     int destination;    // конечная остановка
//     struct Passenger *next;     // указатель на следующего пассажира в списке
// } Passenger;

// typedef struct {
//     Passenger *head;
//     Passenger *tail;
// } PassengerList;

// typedef struct Node {
//     int id;
//     pthread_mutex_t mutex;  // мьютекс для синхронизации доступа
//     pthread_cond_t cond;    // условная переменная для ожидания автобуса
//     PassengerList passengers; // очередь пассажиров на остановке
//     int neighbor_count;       // число соседних узлов
//     int neighbors[MAX_NODES]; // массив идентификаторов соседей
// } Node;

// typedef struct Bus {
//     int id;
//     int capacity;
//     PassengerList passengers;
//     Node *current_node;
// } Bus;



// Node nodes[MAX_NODES];
// int node_count = 0; // количество узлов
// int bus_count = 0;  // количество автобусов
// int passenger_id = 0; // глобальный счётчик пассажиров
// pthread_mutex_t passenger_id_mutex = PTHREAD_MUTEX_INITIALIZER; // мьютекс для безопасного увеличения passenger_id



// void passenger_list_init(PassengerList *list) {
//     list->head = NULL;
//     list->tail = NULL;
// }

// void passenger_list_add(PassengerList *list, Passenger *p) {
//     p->next = NULL;
//     if (list->tail == NULL) {
//         list->head = list->tail = p;
//     } else {
//         list->tail->next = p;
//         list->tail = p;
//     }
// }

// Passenger *passenger_list_remove(PassengerList *list, Passenger *prev, Passenger *current) {
//     if (prev == NULL) {
//         list->head = current->next;
//     } else {
//         prev->next = current->next;
//     }
//     if (current == list->tail) {
//         list->tail = prev;
//     }
//     return current;
// }

// void *bus_thread(void *arg) {
//     Bus *bus = (Bus *)arg;
//     while (1) {
//         Node *current = bus->current_node;
//         pthread_mutex_lock(&current->mutex);

//         int max_count = 0;
//         int selected_neighbor = -1;

//         for (int i = 0; i < current->neighbor_count; ++i) {
//             int neighbor_id = current->neighbors[i];
//             int count = 0;
//             Passenger *p = current->passengers.head;
//             Passenger *prev = NULL;
//             while (p != NULL) {
//                 if (p->destination == neighbor_id) {
//                     count++;
//                 }
//                 prev = p;
//                 p = p->next;
//             }
//             if (count > max_count && count >= bus->capacity / 2) {
//                 max_count = count;
//                 selected_neighbor = neighbor_id;
//             }
//         }

//         while (selected_neighbor == -1) {
//             pthread_cond_wait(&current->cond, &current->mutex);
//             for (int i = 0; i < current->neighbor_count; ++i) {
//                 int neighbor_id = current->neighbors[i];
//                 int count = 0;
//                 Passenger *p = current->passengers.head;
//                 while (p != NULL) {
//                     if (p->destination == neighbor_id) {
//                         count++;
//                     }
//                     p = p->next;
//                 }
//                 if (count > max_count && count >= bus->capacity / 2) {
//                     max_count = count;
//                     selected_neighbor = neighbor_id;
//                 }
//             }
//         }

//         Passenger *p = current->passengers.head;
//         Passenger *prev = NULL;
//         int taken = 0;
//         while (p != NULL && taken < bus->capacity) {
//             if (p->destination == selected_neighbor) {
//                 Passenger *to_remove = p;
//                 p = p->next;
//                 passenger_list_remove(&current->passengers, prev, to_remove);
//                 passenger_list_add(&bus->passengers, to_remove);
//                 taken++;
//             } else {
//                 prev = p;
//                 p = p->next;
//             }
//         }

//         printf("Bus %d departing from node %d to %d with %d passengers\n",
//                bus->id, current->id, selected_neighbor, taken);
//         pthread_mutex_unlock(&current->mutex);

//         sleep(TRAVEL_TIME);

//         Node *dest_node = &nodes[selected_neighbor];
//         pthread_mutex_lock(&dest_node->mutex);
//         Passenger *passenger = bus->passengers.head;
//         Passenger *next;
//         while (passenger != NULL) {
//             next = passenger->next;
//             if (passenger->destination == dest_node->id) {
//                 printf("Passenger %d arrived at destination %d\n",
//                        passenger->id, dest_node->id);
//                 free(passenger);
//             } else {
//                 passenger->source = dest_node->id;
//                 passenger_list_add(&dest_node->passengers, passenger);
//                 printf("Passenger %d transferred to node %d\n",
//                        passenger->id, dest_node->id);
//             }
//             passenger = next;
//         }
//         bus->passengers.head = bus->passengers.tail = NULL;
//         pthread_cond_signal(&dest_node->cond);
//         pthread_mutex_unlock(&dest_node->mutex);

//         bus->current_node = dest_node;
//     }
//     return NULL;
// }

// void *passenger_generator(void *arg) {
//     while (1) {
//         int source = rand() % node_count;
//         int destination;
//         do {
//             destination = rand() % node_count;
//         } while (destination == source);

//         pthread_mutex_lock(&passenger_id_mutex);
//         Passenger *p = malloc(sizeof(Passenger));
//         p->id = passenger_id++;
//         pthread_mutex_unlock(&passenger_id_mutex);
//         p->source = source;
//         p->destination = destination;

//         pthread_mutex_lock(&nodes[source].mutex);
//         passenger_list_add(&nodes[source].passengers, p);
//         printf("Passenger %d created at node %d to %d\n",
//                p->id, source, destination);
//         pthread_cond_signal(&nodes[source].cond);
//         pthread_mutex_unlock(&nodes[source].mutex);

//         sleep(rand() % GENERATION_INTERVAL + 1);
//     }
//     return NULL;
// }

// void load_graph(const char *filename) {
//     FILE *file = fopen(filename, "r");
//     if (!file) {
//         perror("Failed to open file");
//         exit(1);
//     }

//     int edge_count;
//     fscanf(file, "%d %d", &node_count, &edge_count);

//     for (int i = 0; i < node_count; ++i) {
//         nodes[i].id = i;
//         pthread_mutex_init(&nodes[i].mutex, NULL);
//         pthread_cond_init(&nodes[i].cond, NULL);
//         passenger_list_init(&nodes[i].passengers);
//         nodes[i].neighbor_count = 0;

//         for (int j = 0; j < node_count; ++j) {
//             int connected;
//             fscanf(file, "%d", &connected);
//             if (connected && i != j) {
//                 nodes[i].neighbors[nodes[i].neighbor_count++] = j;
//             }
//         }
//     }
//     fclose(file);
// }




#include "bus_sim.h"

int main(int argc, char *argv[]) {
    if (argc < 3) {
        fprintf(stderr, "Usage: %s <graph_file> <bus_count> <bus_capacity>\n", argv[0]);
        return 1;
    }

    srand(time(NULL));
    load_graph(argv[1]);
    bus_count = atoi(argv[2]);
    int bus_capacity = atoi(argv[3]);
    // load_graph("graph_28_3_2.txt");
    // bus_count = atoi("2");
    // int bus_capacity = atoi("4");



    pthread_t buses[bus_count];
    for (int i = 0; i < bus_count; ++i) {
        Bus *bus = malloc(sizeof(Bus));
        bus->id = i;
        bus->capacity = bus_capacity;
        passenger_list_init(&bus->passengers);
        bus->current_node = &nodes[rand() % node_count];
        pthread_create(&buses[i], NULL, bus_thread, bus);
    }

    pthread_t generator;
    pthread_create(&generator, NULL, passenger_generator, NULL);

    pthread_join(generator, NULL);
    for (int i = 0; i < bus_count; ++i) {
        pthread_join(buses[i], NULL);
    }

    return 0;
}