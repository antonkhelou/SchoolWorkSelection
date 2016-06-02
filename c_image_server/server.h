#include <sys/select.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <time.h>
#include <fcntl.h>

#include <pthread.h>

/***********************************************************
 * Constant Parameters
 ***********************************************************/

#define MAX_CLIENTS		100		// Max number of clients supported
#define NUM_WORKER		10		// Number of worker queues
#define NUM_CLASS		4		// Number of classification priority queues
#define MAX_QUEUE_SIZE		100		// Max total queue length of worker queues
#define TIMEOUT_PERIOD		100000		// Timeout period for discarding old work items
#define MAX_RATE		3		// Maximum images per second per client

/***********************************************************
 * STRUCTS
 ***********************************************************/

// Client entries contain information about a given client
typedef struct _client_thread_entry_t
{
    int client_id;				// The client index and ID
    bool is_empty;				// Flag to check if empty
    int client_pid;				// Client PID identifier
    uint32_t ip_addr;				// Client IP address
    int controlfd;				// Control connection file descriptor
    int datafd;					// Data connection file descriptor
    bool connection_closed;			// Flag marking if connection closed
    pthread_t ctl_thread;			// Control and request pthread for this client
    struct _client_thread_entry_t *prev_entry;	// Previous entry
    struct _client_thread_entry_t *next_entry;	// Next entry
} client_thread_entry_t;

// Master database for storing clients entries
typedef struct _client_list_t
{
    client_thread_entry_t entry[MAX_CLIENTS];	// Array of thread entries
    client_thread_entry_t *next_occupied;	// Next entry that is occupied
    client_thread_entry_t *next_available;	// Next entry that is available
} client_list_t;

// Input arguments for connection listener threads
typedef struct _listener_thread_arg_t
{
    int fd;				// File descriptor value
    int client_id;			// Assigned client ID
    int client_pid;			// Client PID used to identify along with address
    uint16_t port;			// client port number (host order)
    uint32_t ip_addr;			// IP address of client (host order)
} listener_thread_arg_t;

// Input arguments for worker threads
typedef struct _worker_thread_arg_t
{
    int worker_id;			// Worker thread ID
} worker_thread_arg_t;

// Work item
typedef struct _send_work_item_t
{
    int client_id;				// Client ID
    int request_id;				// Request ID
    char image_name[256];               	// Image name
    struct timeval timestamp;			// Arrived timestamp;
    struct _send_work_item_t *next_item;	// Next work item in queue
    struct _send_work_item_t *next_common;	// Next work item in queue with same client ID
} send_work_item_t;

/***********************************************************
 * FUNCTIONS
 ***********************************************************/

// Server listener threads
void* dataConnectionListener(void* arg);
void* controlRequestListener(void* arg);
void* tempClientAuthentifier(void* arg);

// Worker (Sending) threads
void* schedulerThread(void* arg);
void* imageWorkerThread(void* arg);

// Client list manipulation functions
void initClientList(client_list_t *client_list);
int requestClientEntry(client_list_t *client_list);
int relinquishClientEntry(client_list_t *client_list, int client_id);

// Dispatch functions to scheduler
int findClass(int panning_speed);
int postWorkItem(send_work_item_t* work_item);

// Socket read helper functions
int readInteger(int fd, int *out);
int readString(int fd, char *buf, size_t size);


