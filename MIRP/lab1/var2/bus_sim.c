#include "bus_sim.h"
#include <stdarg.h>
#include <errno.h>

volatile Node nodes[MAX_NODES];
volatile int node_count = 0;
volatile int bus_count = 0;
volatile int passenger_id = 0;
volatile double sim_time = 0.0;
volatile pthread_mutex_t passenger_id_mutex = PTHREAD_MUTEX_INITIALIZER;
volatile pthread_mutex_t time_mutex = PTHREAD_MUTEX_INITIALIZER;
volatile pthread_mutex_t log_mutex = PTHREAD_MUTEX_INITIALIZER;

void log_event(const char *event_type, const char *format, ...) {
    va_list args;
    va_start(args, format);
    
    pthread_mutex_lock(&log_mutex);
    FILE *log_file = fopen("simulation.log", "a");
    if (log_file == NULL) {
        perror("Failed to open log file");
        return;
    }
    
    pthread_mutex_lock(&time_mutex);
    fprintf(log_file, "[%.2f] %s: ", sim_time, event_type);
    pthread_mutex_unlock(&time_mutex);
    
    vfprintf(log_file, format, args);
    fprintf(log_file, "\n");
    fclose(log_file);
    
    pthread_mutex_unlock(&log_mutex);
    va_end(args);

    fclose(log_file);
}

void passenger_list_init(PassengerList *list) {
    list->head = list->tail = NULL;
    list->count = 0;
}

void passenger_list_add(PassengerList *list, Passenger *p) {
    p->next = NULL;
    if (list->tail == NULL) {
        list->head = list->tail = p;
    } else {
        list->tail->next = p;
        list->tail = p;
    }
    list->count++;
}

Passenger *passenger_list_remove(PassengerList *list, Passenger *prev, Passenger *current) {
    if (prev == NULL) {
        list->head = current->next;
    } else {
        prev->next = current->next;
    }
    if (current == list->tail) {
        list->tail = prev;
    }
    list->count--;
    return current;
}

void *bus_thread(void *arg) {
    Bus *bus = (Bus *)arg;
    while (1) {
        Node *current = bus->current_node;
        
        // Захват мьютекса с таймаутом
        struct timespec timeout;
        clock_gettime(CLOCK_REALTIME, &timeout);
        timeout.tv_sec += MUTEX_TIMEOUT;
        if (pthread_mutex_timedlock(&current->mutex, &timeout) != 0) {
            log_event("ERROR", "Bus %d: Failed to lock node %d", bus->id, current->id);
            continue; // Пропустить итерацию, если мьютекс не захвачен
        }

        // Выбор направления
        int max_count = 0, selected_neighbor = -1;
        for (int i = 0; i < current->neighbor_count; ++i) {
            int neighbor_id = current->neighbors[i];
            int count = 0;
            Passenger *p = current->passengers.head;
            while (p != NULL) {
                if (p->destination == neighbor_id) count++;
                p = p->next;
            }
            if (count > max_count && count >= bus->capacity / 2) {
                max_count = count;
                selected_neighbor = neighbor_id;
            }
        }

        
        // Принудительный выбор, если есть пассажиры (даже < k/2)
        if (selected_neighbor == -1 && current->passengers.count > 0) {
            selected_neighbor = current->neighbors[0]; // Первый сосед в списке
            log_event("BUS_FORCE_DEPART", "Bus %d: Forced depart (passengers: %d)", 
                bus->id, current->passengers.count);
        }

        if (selected_neighbor == -1 && current->passengers.count > 0 && current->neighbor_count > 0) {
            selected_neighbor = current->neighbors[0];
        }
        
        
        
        // Ожидание пассажиров
        while (selected_neighbor == -1) {
            log_event("BUS_WAIT", "Bus %d waiting at node %d", bus->id, current->id);
            pthread_cond_wait(&current->cond, &current->mutex);
            // Перепроверка условия после пробуждения
            for (int i = 0; i < current->neighbor_count; ++i) {
                int neighbor_id = current->neighbors[i];
                int count = 0;
                Passenger *p = current->passengers.head;
                while (p != NULL) {
                    if (p->destination == neighbor_id) count++;
                    p = p->next;
                }
                if (count >= bus->capacity / 2) {
                    selected_neighbor = neighbor_id;
                    break;
                }
            }
        }

        // Посадка пассажиров
        int taken = 0;
        Passenger *p = current->passengers.head, *prev = NULL;
        while (p != NULL && taken < bus->capacity) {
            if (p->destination == selected_neighbor) {
                Passenger *to_remove = p;
                p = p->next;
                passenger_list_remove(&current->passengers, prev, to_remove);
                passenger_list_add(&bus->passengers, to_remove);
                taken++;
            } else {
                prev = p;
                p = p->next;
            }
        }

        log_event("BUS_DEPART", "Bus %d: %d -> %d, passengers: %d/%d", 
                 bus->id, current->id, selected_neighbor, taken, bus->capacity);
        pthread_mutex_unlock(&current->mutex);

        sleep(TRAVEL_TIME);
        pthread_mutex_lock(&time_mutex);
        sim_time += TRAVEL_TIME;
        pthread_mutex_unlock(&time_mutex);

        // Прибытие
        Node *dest_node = &nodes[selected_neighbor];
        pthread_mutex_lock(&dest_node->mutex);
        int disembarked = 0;
        Passenger *passenger = bus->passengers.head, *next;
        while (passenger != NULL) {
            next = passenger->next;
            if (passenger->destination == dest_node->id) {
                log_event("PASSENGER_ARRIVE", "Passenger %d: %d -> %d (travel time: %.2f)", 
                          passenger->id, passenger->source, dest_node->id, sim_time - passenger->creation_time);
                free(passenger);
                disembarked++;
            } else {
                passenger->source = dest_node->id;
                passenger_list_add(&dest_node->passengers, passenger);
                log_event("PASSENGER_TRANSFER", "Passenger %d transferred to node %d", 
                          passenger->id, dest_node->id);
            }
            passenger = next;
        }
        bus->passengers.head = bus->passengers.tail = NULL;
        bus->passengers.count = 0;
        log_event("BUS_ARRIVE", "Bus %d arrived at %d, disembarked: %d", 
                 bus->id, dest_node->id, disembarked);
        pthread_cond_signal(&dest_node->cond);
        pthread_mutex_unlock(&dest_node->mutex);
        bus->current_node = dest_node;
    }
    return NULL;
}

void *passenger_generator(void *arg) {
    while (1) {
        int source = rand() % node_count;
        int destination;
        do { destination = rand() % node_count; } while (destination == source);

        pthread_mutex_lock(&passenger_id_mutex);
        Passenger *p = malloc(sizeof(Passenger));
        p->id = passenger_id++;
        pthread_mutex_lock(&time_mutex);
        p->creation_time = sim_time;
        pthread_mutex_unlock(&time_mutex);
        pthread_mutex_unlock(&passenger_id_mutex);
        p->source = source;
        p->destination = destination;

        pthread_mutex_lock(&nodes[source].mutex);
        passenger_list_add(&nodes[source].passengers, p);
        log_event("PASSENGER_CREATE", "Passenger %d at node %d -> %d", p->id, source, destination);
        pthread_cond_signal(&nodes[source].cond);
        pthread_mutex_unlock(&nodes[source].mutex);

        sleep(rand() % (GENERATION_INTERVAL / 2) + 1);
    }
    return NULL;
}

void *time_updater(void *arg) {
    while (1) {
        pthread_mutex_lock(&time_mutex);
        sim_time += 1.0;
        pthread_mutex_unlock(&time_mutex);
        sleep(1); // Непрерывное обновление времени
    }
}

void *statistics_thread(void *arg) {
    while (1) {
        sleep(STATS_INTERVAL);
        pthread_mutex_lock(&time_mutex);
        log_event("STATS", "--- Simulation time: %.2f ---", sim_time);
        for (int i = 0; i < node_count; i++) {
            pthread_mutex_lock(&nodes[i].mutex);
            log_event("NODE_STATS", "Node %d: %d passengers", i, nodes[i].passengers.count);
            pthread_mutex_unlock(&nodes[i].mutex);
        }
        pthread_mutex_unlock(&time_mutex);
    }
}

void load_graph(const char *filename) {
    FILE *file = fopen(filename, "r");
    if (!file) {
        perror("Failed to open file");
        exit(1);
    }

    int edge_count;
    fscanf(file, "%d %d", &node_count, &edge_count);

    for (int i = 0; i < node_count; ++i) {
        nodes[i].id = i;
        pthread_mutex_init(&nodes[i].mutex, NULL);
        pthread_cond_init(&nodes[i].cond, NULL);
        passenger_list_init(&nodes[i].passengers);
        nodes[i].neighbor_count = 0;

        for (int j = 0; j < node_count; ++j) {
            int connected;
            fscanf(file, "%d", &connected);
            if (connected && i != j) {
                nodes[i].neighbors[nodes[i].neighbor_count++] = j;
            }
        }
    }
    fclose(file);
}