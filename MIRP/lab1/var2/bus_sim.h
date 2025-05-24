#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <time.h>
#include <stdbool.h>
#include <sys/time.h>

#define MAX_NODES 10
#define TRAVEL_TIME 1
#define GENERATION_INTERVAL 2
#define STATS_INTERVAL 5
#define MUTEX_TIMEOUT 2

typedef struct Passenger {
    int id;
    int source;
    int destination;
    double creation_time;
    struct Passenger *next;
} Passenger;

typedef struct {
    Passenger *head;
    Passenger *tail;
    int count;
} PassengerList;

typedef struct Node {
    int id;
    pthread_mutex_t mutex;
    pthread_cond_t cond;
    PassengerList passengers;
    int neighbor_count;
    int neighbors[MAX_NODES];
} Node;

typedef struct Bus {
    int id;
    int capacity;
    PassengerList passengers;
    Node *current_node;
} Bus;

extern volatile Node nodes[MAX_NODES];
extern volatile int node_count;
extern volatile int bus_count;
extern volatile int passenger_id;
extern volatile double sim_time;
extern volatile pthread_mutex_t passenger_id_mutex;
extern volatile pthread_mutex_t time_mutex;
extern volatile pthread_mutex_t log_mutex;

void log_event(const char *event_type, const char *format, ...);
void passenger_list_init(PassengerList *list);
void passenger_list_add(PassengerList *list, Passenger *p);
Passenger *passenger_list_remove(PassengerList *list, Passenger *prev, Passenger *current);
void *bus_thread(void *arg);
void *passenger_generator(void *arg);
void *time_updater(void *arg);
void *statistics_thread(void *arg);
void load_graph(const char *filename);