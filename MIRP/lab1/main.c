 
/* АПИМ-24, Разуваев, Вариант 28
Задача: Пункты A, B, C, … связаны автобусным сообщением в соответствии с заданным графом. 
Автобус имеют вместимость k пассажиров и отправляются, если в нем занято не менее k/2 мест. 
Пассажиры появляются в пунктах посадки случайно или для пересадки на другой автобус.
*/


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