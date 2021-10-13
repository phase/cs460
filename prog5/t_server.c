#include "t_server.h"

/************************************************************************
 * MAIN
 ************************************************************************/

/* ******************************************************* */
/* prepare arguments for thread function                   */
/* ******************************************************* */
void *task_copy_arguments(void *args_in) {
    void *args_out;

    args_out = malloc(sizeof(int));
    *((int*)args_out) = *((int*)args_in);

    return args_out;
}

int main(int argc, char** argv) {
    int server_socket;                 // descriptor of server socket
    struct sockaddr_in server_address; // for naming the server's listening socket
    int client_socket;

    threadpool pool = threadpool_create();
    // sent when ,client disconnected
    signal(SIGPIPE, SIG_IGN);

    // create unnamed network socket for server to listen on
    if ((server_socket = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Error creating socket");
        exit(EXIT_FAILURE);
    }

    // name the socket (making sure the correct network byte ordering is observed)
    server_address.sin_family      = AF_INET;           // accept IP addresses
    server_address.sin_addr.s_addr = htonl(INADDR_ANY); // accept clients on any interface
    server_address.sin_port        = htons(PORT);       // port to listen on

    // binding unnamed socket to a particular port
    if (bind(server_socket, (struct sockaddr *)&server_address, sizeof(server_address)) == -1) {
        perror("Error binding socket");
        exit(EXIT_FAILURE);
    }

    // listen for client connections (pending connections get put into a queue)
    if (listen(server_socket, NUM_CONNECTIONS) == -1) {
        perror("Error listening on socket");
        exit(EXIT_FAILURE);
    }

    // server loop
    while (TRUE) {
        // accept connection to client
        if ((client_socket = accept(server_socket, NULL, NULL)) == -1) {
            perror("Error accepting client");
        } else {
            printf("\nAccepted client\n");
            // allocating the arg being passed into the thread and declaring it
            int *cs = malloc(sizeof(*cs));
            *cs = client_socket;
            threadpool_add_task(pool, task_copy_arguments, handle_client, (void*)cs);
            usleep(1000);
        }
    }
}

/************************************************************************
 * handle client
 ************************************************************************/

void *handle_client(void *args) {
    int client_socket = *((int *) args);

    int input;
    int steps;

    // read int from client
    switch(read(client_socket, &input, sizeof(int)))
    {
      case 0:
          printf("\nEnd of stream, returning...\n");
          return NULL;
          break;
      case -1:
          printf("Error reading from network!\n");
          return NULL;
          break;
    }

    printf("Number from client: %d\n", input);
    steps = three_a_plus_one(input);
    printf("Took %d steps\n", steps);

    write(client_socket, &steps, sizeof(int));

    free(args);

    close(client_socket);

    return (void*) input;
}

int collatCounter(int input)
{
  int counter = 0;

  while( input != 1 )
  {
    input = three_a_plus_one(input);
    counter += 1;
  }

  return counter;
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
