#include "client.h"
#include "server.h"
#include "threadpool.h"

/* ******************************************************* */
/* main()                                                  */
/* ******************************************************* */
int main() {

    // task counter, also serves as argument to 3A+1 algorithm
    int task_counter;

    // create threadpool with THREADS_IN_POOL threads
    threadpool pool = threadpool_create();


    for (task_counter=1; task_counter<=NUMBER_TASKS; task_counter++)
    {
        // in each loop, execute three_a_plus_one_wrapper in a thread from the pool
        int* task_num_ptr = (int*) malloc(sizeof(int));
        *task_num_ptr = task_counter;
        threadpool_add_task(pool, task_copy_arguments, three_a_plus_one_wrapper, (void*)task_num_ptr);
        usleep(1000);
    }

    // lame way to wait for everybody to get done
    // in a network server, this is not needed as the main thread keeps accepting connections
    sleep(8);

    exit(EXIT_SUCCESS);
}


/* ******************************************************* */
/* three_a_plus_one_wrapper()                              */
/* ******************************************************* */
void three_a_plus_one_wrapper(void *number_ptr)
{
    int client_socket;
    struct sockaddr_in client_address;
    int number = *((int*)number_ptr);
    int num_of_steps;

    client_socket = socket(AF_INET, SOCK_STREAM, 0);

    // create addr struct
    client_address.sin_family = AF_INET;
    client_address.sin_addr.s_addr = inet_addr(SERVER_ADDR);
    client_address.sin_port = htons(PORT);

    if (connect(client_socket, (struct sockaddr *)&client_address, sizeof(client_address)) == -1)
    {
        perror("Error connecting to server!\n");
        exit(EXIT_FAILURE);
    } else {
        write(client_socket, number_ptr, sizeof(int));
        read(client_socket, &num_of_steps, sizeof(int));

        close(client_socket);

        printf("Number of steps: %d\n", num_of_steps);
        printf("\nthread ID %p ----> %d: %d\n", pthread_self(), number, num_of_steps);
    }
}


/* ******************************************************* */
/* three_a_plus_one() - nonrecursive                       */
/* ******************************************************* */
int three_a_plus_one(int input)
{
    int counter = 0;
    int current = input;

    while (current != 1)
    {
        counter++;
        if (current % 2) {
            current = (current*3) + 1;
        }
        else {
            current >>= 1;
        }
    }
    return counter;
}


/* ******************************************************* */
/* three_a_plus_one_rec() - recursive                          */
/* ******************************************************* */
int three_a_plus_one_rec(int number) {
    int new_number;

    if (number == 1) {
        return 0;
    }

    if (number % 2) {
        new_number = 3 * number + 1;
    } else {
        new_number = number / 2;
    }

    return 1 + three_a_plus_one(new_number);
}


/* ******************************************************* */
/* prepare arguments for thread function                   */
/* ******************************************************* */
void *task_copy_arguments(void *args_in)
{
    void *args_out;

    args_out = malloc(sizeof(int));
    *((int*)args_out) = *((int*)args_in);

    return args_out;
}
