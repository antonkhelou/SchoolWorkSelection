#include "server.h"

/***********************************************************
 * Global Variables
 ***********************************************************/

// Primary Threads
pthread_t data_connection_listener;		// Data connection listener thread
pthread_t scheduler_thread;			// Scheduler thread

// Client list variables
pthread_mutex_t client_db_mutex;		// Mutex for the following variables
client_list_t client_db;			// Database of registered clients
int num_clients = 0;				// Number of clients registered
bool dataconnection_ready[MAX_CLIENTS];		// Flags for waiting data connections
int panning_speed[MAX_CLIENTS];			// Panning speed of clients
int data_ports[MAX_CLIENTS];			// Data connection ports for clients

// Classification priority queues
pthread_mutex_t class_queue_mutex[NUM_CLASS];	// Mutex array for following variables
send_work_item_t *class_queue_head[NUM_CLASS];	// Heads of classification priority queues
send_work_item_t *class_queue_tail[NUM_CLASS];	// Tails of classification priority queues
int class_queue_length[NUM_CLASS];		// Queue length of classification priority queues

// Worker threads and worker queues
pthread_t worker_threads[NUM_WORKER];			// Worker threads
pthread_mutex_t worker_queue_mutex[NUM_WORKER];		// Mutex array for following variables
send_work_item_t *worker_queue_head[NUM_WORKER];	// Heads of worker queues
send_work_item_t *worker_queue_tail[NUM_WORKER];	// Tails of worker queues
int worker_queue_length[NUM_WORKER];			// Queue length of worker queues

// Client queues used to link all work item of same client
// These queues are used to guarantee that a given client is served by only
// one worker thread at a time
pthread_mutex_t client_queue_mutex[MAX_CLIENTS];	// Mutex array for following variables
send_work_item_t *client_queue_head[MAX_CLIENTS];	// Heads of client queues
send_work_item_t *client_queue_tail[MAX_CLIENTS];	// Tails of client queues
int client_queue_length[MAX_CLIENTS];			// Queue length of client queues
int client_active_worker[MAX_CLIENTS];			// Worker thread associated with each client

/***********************************************************
 * MAIN THREAD
 ***********************************************************/

int main(int argc, char *argv[])
{
    int datafd = 0, controlfd = 0, connection_fd = 0, i = 0;
    int dataport_num, controlport_num, client_id;
    struct sockaddr_in data_addr, ctl_addr, client_addr;
    int client_addr_length = 0;
    listener_thread_arg_t *params = NULL;
    pthread_t data_connection_thread;

    // Parse control port number from 1st argument
    if (argc < 3)
    {
        fprintf(stderr,"ERROR: Not enough port numbers provided in argument!\n");
        exit(1);
    }
    controlport_num = atoi(argv[1]);
    if (controlport_num < 1)
    {
        fprintf(stderr,"ERROR: Invalid control port number!\n");
        exit(1);
    }
    printf("Control port# %d\n",controlport_num);

    // Parse data port number from 2nd argument
    dataport_num = atoi(argv[2]);
    if (dataport_num < 1)
    {
        fprintf(stderr,"ERROR: Invalid data port number!\n");
        exit(1);
    }
    printf("Data port# %d\n",dataport_num);

    // Initialize client list
    initClientList(&client_db);
    for (i=0; i<MAX_CLIENTS; ++i)
    {
        panning_speed[i] = 1;
        data_ports[i] = 0;
        dataconnection_ready[i] = false;
    }

    // Initialize worker, class and client queues + variables
    for (i=0; i<NUM_WORKER; ++i)
    {
        worker_queue_head[i] = NULL;
        worker_queue_tail[i] = NULL;
        worker_queue_length[i] = 0;
    }
    for (i=0; i<NUM_CLASS; ++i)
    {
        class_queue_head[i] = NULL;
        class_queue_tail[i] = NULL;
        class_queue_length[i] = 0;
    }
    for (i=0; i<MAX_CLIENTS; ++i)
    {
        client_queue_head[i] = NULL;
        client_queue_tail[i] = NULL;
        client_queue_length[i] = 0;
        client_active_worker[i] = -1;
    }

    // Create data socket
    datafd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (datafd < 0)
    {
        fprintf(stderr,"ERROR: Cannot create data socket!\n");
        exit(1);
    }
    // Set data socket address/port
    memset(&data_addr, '0', sizeof(data_addr));
    data_addr.sin_family = AF_INET;
    data_addr.sin_addr.s_addr = htonl(INADDR_ANY);	// Any interface
    data_addr.sin_port = htons(dataport_num);
    // Bind data socket with address/port
    if (bind(datafd, (struct sockaddr*)&data_addr, sizeof(data_addr))<0)
    {
        fprintf(stderr,"ERROR: Cannot bind data socket!\n");
        exit(1);
    } 

    // Create control socket
    controlfd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (controlfd < 0)
    {
        fprintf(stderr,"ERROR: Cannot create control socket!\n");
        exit(1);
    }
    // Set control socket address
    memset(&ctl_addr, '0', sizeof(ctl_addr));
    ctl_addr.sin_family = AF_INET;
    ctl_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    ctl_addr.sin_port = htons(controlport_num); 
    // Bind control socket with address/port
    if (bind(controlfd, (struct sockaddr*)&ctl_addr, sizeof(ctl_addr))<0)
    {
        fprintf(stderr,"ERROR: Cannot bind control socket!\n");
        exit(1);
    }

    // Listen to socket connections
    listen(datafd, MAX_CLIENTS);
    listen(controlfd, MAX_CLIENTS);

    // Spawn a thread to listen to data socket connections
    params = malloc(sizeof(listener_thread_arg_t));
    params->fd = datafd;
    if (pthread_create(&data_connection_thread, NULL, dataConnectionListener, (void*)params) != 0)
    {
        fprintf(stderr,"ERROR: Cannot create data connection listener thread!\n");
        exit(1);
    }

    // Spawn a thread for scheduler
    if (pthread_create(&scheduler_thread, NULL, schedulerThread, NULL) != 0)
    {
        fprintf(stderr,"ERROR: Cannot create scheduler thread!\n");
        exit(1);
    }

    // Spawn all the worker threads
    for (i=0; i<NUM_WORKER; ++i)
    {
        worker_thread_arg_t *worker_params = malloc(sizeof(worker_thread_arg_t));
        worker_params->worker_id = i;
        if (pthread_create(&(worker_threads[i]), NULL, imageWorkerThread, (void*)worker_params) != 0)
        {
            fprintf(stderr,"ERROR: Cannot create worker thread #%d!\n",i);
            exit(1);
        }
    }
    printf("%d worker threads created succesfully\n",NUM_WORKER);

    // Use the rest of the main thread to accept new client connections
    while(1)
    {
        if(client_db.next_available != NULL)	// If not already full
        {
            client_addr_length = sizeof(client_addr);
            connection_fd = accept(controlfd, (struct sockaddr*)&client_addr, &client_addr_length);
            if (connection_fd < 0)
            {
                fprintf(stderr,"ERROR: Control connection accept failed!\n");
            }
            else
            {
                // Try to request an available slot in client list
                client_id = requestClientEntry(&client_db);
                if (client_id < 0)  // If all slots occupied
                {
                    printf("Client number exceeded!\n");
                    close(connection_fd);
                    continue;
                }
                // Get the IP/port information from connection
                params = malloc(sizeof(listener_thread_arg_t));
                params->port = ntohs(client_addr.sin_port);
                params->ip_addr = ntohl(client_addr.sin_addr.s_addr);
                params->fd = connection_fd;
                params->client_id = client_id;

                client_db.entry[client_id].connection_closed = false;
                // Create thread to handle the client
                if (pthread_create(&(client_db.entry[client_id].ctl_thread), NULL, 
                                      controlRequestListener, (void*)params) != 0)
                {
                    fprintf(stderr,"ERROR: Cannot create thread!\n");
                    client_db.entry[client_id].connection_closed = true;
                    relinquishClientEntry(&client_db,client_id);                    
                    close(connection_fd);
                    free(params);
                }
            }
        }
    }
}

/***********************************************************
 * SERVER LISTENER THREADS
 ***********************************************************/

/*
 * Thread that listens to data socket connections
 */
void* dataConnectionListener(void* arg)
{
    listener_thread_arg_t *params = (listener_thread_arg_t*) arg;
    listener_thread_arg_t *new_params = NULL;
    int datafd = params->fd;
    int connection_fd = 0;
    struct sockaddr_in client_addr;
    int client_addr_length = 0;
    pthread_t temp_thread;

    while(1)
    {
        connection_fd = accept(datafd, (struct sockaddr*)&client_addr, &client_addr_length);
        if (connection_fd < 0)
        {
            fprintf(stderr,"ERROR: Data connection accept failed!\n");
        }
        else
        {
            // Get the IP/port information from connection
            new_params = malloc(sizeof(listener_thread_arg_t));
            new_params->port = ntohs(client_addr.sin_port);
            new_params->ip_addr = ntohl(client_addr.sin_addr.s_addr);
            new_params->fd = connection_fd;

            // Launch a temporary data connection handler
            if (pthread_create(&temp_thread, NULL, tempClientAuthentifier, 
                                  (void*)new_params) != 0)
            {
                fprintf(stderr,"ERROR: Cannot create thread!\n");                
                close(connection_fd);
                free(new_params);
            }
        }
    }
    return NULL;
}

/*
 * Thread that listens to both panning speed control signals
 * and requests for a given client
 */
void* controlRequestListener(void* arg)
{
    listener_thread_arg_t *params = (listener_thread_arg_t*) arg;
    int controlfd = params->fd;
    int datafd = 0;
    char tmpBuff[32], sendBuff[32], fileName[256];
    int buf_len = 0, total_bytes_read = 0;
    int recv_pid = -1;
    bool data_closed = false, ctl_closed = false;
    fd_set client_fd_set;

    // Initialize the select file descriptors
    FD_ZERO(&client_fd_set);

    // Set control file descriptor in global variable
    client_db.entry[params->client_id].controlfd = controlfd;
    
    // Wait for PID info from control socket and check for validity
    if (readInteger(controlfd, &recv_pid) <= 0)
    {
        fprintf(stderr,"ERROR: Failed PID read!\n");
        goto End;
    }
    if (recv_pid < 0)
    {
        fprintf(stderr,"ERROR: Wrong PID range!\n");
        goto End;
    }

    printf("==============================\n>> Client ID: %d\n>> Addr: %d.%d.%d.%d:%d\n>> PID: %d\n",
           params->client_id, (params->ip_addr >> 24), (params->ip_addr >> 16)&0xFF, 
           (params->ip_addr >> 8)&0xFF, (params->ip_addr)&0xFF, params->port, recv_pid);

    // Register PID and IP
    pthread_mutex_lock(&client_db_mutex);
    client_db.entry[params->client_id].client_pid = recv_pid;
    client_db.entry[params->client_id].ip_addr = params->ip_addr;
    pthread_mutex_unlock(&client_db_mutex);

    // Send echo back
    memset(sendBuff, 0 , strlen(sendBuff));
    snprintf(sendBuff, sizeof(sendBuff), "%i\n", recv_pid);
    if (write(controlfd, sendBuff, strlen(sendBuff))<0)
    {
        fprintf(stderr,"ERROR: Write to socket failed!\n");
        goto End;
    }

    // Wait until data connection is established
    printf("Waiting for data connection from client %d...\n",params->client_id);
    while(dataconnection_ready[params->client_id] == false);
    dataconnection_ready[params->client_id] = false;
    datafd = client_db.entry[params->client_id].datafd;
    printf("Client %d data connection established at port %d!\n",params->client_id, 
            data_ports[params->client_id]);

    // Use select to watch both data and control requests
    while (!data_closed && !ctl_closed)
    {
        if(!data_closed) FD_SET(datafd, &client_fd_set);
        if(!ctl_closed) FD_SET(controlfd, &client_fd_set);
        if (select(((controlfd>datafd)?controlfd:datafd)+1, &client_fd_set, NULL, NULL, NULL) < 0)
        {
            fprintf(stderr,"ERROR: Select failed!\n");
            continue;
        }
        // If the control socket is triggered
        if (FD_ISSET(controlfd,&client_fd_set))
        {
            int recv_speed = 0;            
            if (!(ctl_closed = (readInteger(controlfd,&recv_speed)<=0)))
            {
                // TODO: Do some checks on panning speed bounds
                //printf("Client %d panning update: %d/s\n",params->client_id,recv_speed);
                panning_speed[params->client_id] = recv_speed;
            }
            else
            {
                client_db.entry[params->client_id].connection_closed = true;
            }
        }
        // If the data socket is triggered
        if (FD_ISSET(datafd,&client_fd_set))
        {
            int recv_rqstid = 0;
            struct timeval frozen_time;
            gettimeofday(&frozen_time, NULL);
            memset(fileName, 0, strlen(fileName));

            // Read Request ID
            if (!(data_closed = (readInteger(datafd, &recv_rqstid) <= 0)))
            {                
                // Read the image name string
                if (!(data_closed = (readString(datafd,fileName,sizeof(fileName))<=0)))
                {
                    //printf("Received request for %s from client %d\n", fileName, params->client_id);
                    send_work_item_t *work_item = malloc(sizeof(send_work_item_t));
                    work_item->client_id = params->client_id;
                    work_item->request_id = recv_rqstid;
                    snprintf(work_item->image_name, sizeof(work_item->image_name), "%s", fileName);
                    work_item->timestamp = frozen_time;
                    work_item->next_item = NULL;
                    work_item->next_common = NULL;
                    // Attempt to put the work item in priority queues
                    if (postWorkItem(work_item) < 0)
                    {
                        printf("Client %d: Request %d for %s dropped!\n", 
                               params->client_id, recv_rqstid, fileName);
                        free(work_item);
                    }
                }
            }            
            if (data_closed)
            {
                client_db.entry[params->client_id].connection_closed = true;
            }
        }
    }

End:
    // Release the entry from client_db when client disconnects
    client_db.entry[params->client_id].connection_closed = true;
    if (relinquishClientEntry(&client_db,params->client_id) < 0)
        fprintf(stderr,"ERROR: Cannot relinquish client entry %d\n",params->client_id);
    printf("Client ID %d disconnected\n",params->client_id);

    close(controlfd);
    close(datafd);
    free(params);
    pthread_exit(NULL);
    return NULL;
}

/*
 * Temporary thread to associate a data request connection 
 * with its control connection
 */
void* tempClientAuthentifier(void* arg)
{
    listener_thread_arg_t *params = (listener_thread_arg_t*) arg;
    int datafd = params->fd;
    char sendBuff[1024];
    int buf_len = 0, total_bytes_read = 0;
    int recv_pid = -1;
    client_thread_entry_t *search_entry = NULL;
    bool entry_found = false;

    // Wait for PID info
    if (readInteger(datafd, &recv_pid) <= 0)
    {
        fprintf(stderr,"ERROR: Failed PID read!\n");
        goto Error;
    }
    // Check for PID validity
    if (recv_pid < 0)
    {
        fprintf(stderr,"ERROR: Wrong PID range!\n");
        goto Error;
    }

    // Find IP and PID entry corresponding to this data connection
    // Only search among non-empty entries
    pthread_mutex_lock(&client_db_mutex);
    search_entry = client_db.next_occupied;
    while (search_entry != NULL && !entry_found)
    {
        if((search_entry->client_pid == recv_pid) && 
            (search_entry->client_pid == recv_pid))
        {
            entry_found = true;
            params->client_id = search_entry->client_id;
            search_entry->datafd = datafd;
            break;
        }
        search_entry = search_entry->next_entry;
    }
    pthread_mutex_unlock(&client_db_mutex);
    if (!entry_found)
    {
        fprintf(stderr,"ERROR: Cannot find matching entry for data connection!\n");
        goto Error;
    }

    // Signal the data connection is ready
    data_ports[params->client_id] = params->port;
    dataconnection_ready[params->client_id] = true;

    // Send PID echo back to client
    memset(sendBuff, 0, strlen(sendBuff));
    snprintf(sendBuff, sizeof(sendBuff), "%i\n", recv_pid);
    if(write(datafd, sendBuff, strlen(sendBuff)) < 0)
    {
        fprintf(stderr,"ERROR: Write to socket failed!\n");
        goto Error;
    }
End:
    free(params);    
    pthread_exit(NULL);
    return NULL;
Error:
    close(datafd);
    goto End;
}

/***********************************************************
 * SCHEDULER THREAD
 ***********************************************************/

void* schedulerThread(void* arg)
{
    int i = 0;
    int worker_turn = 0, active_worker = 0;
    int client_id = 0;
    int drop_threshold = 1000;
    bool dropped = false;
    send_work_item_t *temp_work_item = NULL;
    // Queue-length based admission control factor
    int w_factor = (MAX_QUEUE_SIZE*MAX_QUEUE_SIZE)/(NUM_WORKER*NUM_WORKER);

    while(1)
    {
        // Loop through each class queue fairly
        for (i = 0; i < NUM_CLASS; ++i)
        {
            // Step 1: Try to fetch the head of a class queue
            if ((temp_work_item = class_queue_head[i]) != NULL)
            {
                pthread_mutex_lock(&(class_queue_mutex[i]));
                temp_work_item = class_queue_head[i];
                if (temp_work_item != NULL)
                {
                    class_queue_head[i] = temp_work_item->next_item;
                    if (class_queue_head[i] == NULL) class_queue_tail[i] = NULL;
                    class_queue_length[i]--;
                    temp_work_item->next_item = NULL;
                }
                pthread_mutex_unlock(&(class_queue_mutex[i]));
            }
            if (temp_work_item == NULL) continue;

            // Step 2: Find existing worker thread to prevent the same client being 
            // worked on two different threads
            pthread_mutex_lock(&(client_queue_mutex[temp_work_item->client_id]));
            if ((active_worker = client_active_worker[temp_work_item->client_id]) < 0)
            {
                client_active_worker[temp_work_item->client_id] = worker_turn;
                active_worker = worker_turn;
            }
            pthread_mutex_unlock(&(client_queue_mutex[temp_work_item->client_id]));

            // Step 3: Do some admission control again to prevent growing queues
            // Lower panning rate are less likely to be dropped once again
            dropped = false;
            pthread_mutex_lock(&(worker_queue_mutex[active_worker]));
            int w_length = worker_queue_length[active_worker];
            int rate_factor = (2*(panning_speed[temp_work_item->client_id]-(MAX_RATE-1)))/3;
            drop_threshold = rate_factor*(w_length*w_length*1000)/w_factor;
            if (rand()%1000 < drop_threshold)
            {
                dropped = true;
                printf("Scheduler: Client ID %d %s request dropped!\n", temp_work_item->client_id, 
                            temp_work_item->image_name);
                free(temp_work_item);
            }
            pthread_mutex_unlock(&(worker_queue_mutex[active_worker]));

            // The following steps are only valid if work item is not dropped
            if (!dropped)
            {
                // Step 4: Put the temporary work item in its corresponding client queue
                client_id = temp_work_item->client_id;
                pthread_mutex_lock(&(client_queue_mutex[client_id]));
                if (client_queue_tail[client_id] != NULL)
                {
                    client_queue_tail[client_id]->next_common = temp_work_item;
                    client_queue_tail[client_id] = temp_work_item;
                }
                else
                {
                    client_queue_tail[client_id] = temp_work_item;
                    client_queue_head[client_id] = temp_work_item;
                }
                client_queue_length[client_id]++;
                pthread_mutex_unlock(&(client_queue_mutex[client_id]));

                // Step 5: Put the work item in the worker queue
                pthread_mutex_lock(&(worker_queue_mutex[active_worker]));            
                if (worker_queue_tail[active_worker] != NULL)
                {
                    worker_queue_tail[active_worker]->next_item = temp_work_item;
                    worker_queue_tail[active_worker] = temp_work_item;
                }
                else
                {
                    worker_queue_tail[active_worker] = temp_work_item;
                    worker_queue_head[active_worker] = temp_work_item;
                }
                worker_queue_length[active_worker]++;
                /*printf("Scheduler: %s for client %d given to worker %d\n", temp_work_item->image_name,
                       temp_work_item->client_id, active_worker);*/
                pthread_mutex_unlock(&(worker_queue_mutex[active_worker]));

                // Increment the worker_turn counter (with wrap around)
                if (active_worker == worker_turn) worker_turn = (worker_turn+1)%NUM_WORKER;
            }
        }
    }
Error:
    pthread_exit(NULL);
    return NULL;
}

/***********************************************************
 * WORKER THREAD
 ***********************************************************/

/*
 * Worker thread that actually sends the image over the socket
 */
void* imageWorkerThread(void* arg)
{
    worker_thread_arg_t *params = (worker_thread_arg_t*) arg;
    int worker_id = params->worker_id;
    send_work_item_t *work_item = NULL;
    char sendBuff[1024];
    char file_path[512];

    while(1)
    {
        // Step 1: Fetch a work item from the worker queue
        if ((work_item = worker_queue_head[worker_id]) != NULL)
        {
            pthread_mutex_lock(&(worker_queue_mutex[worker_id]));
            if ((work_item = worker_queue_head[worker_id]) != NULL)
            {
                if (work_item->next_item == NULL)
                {
                    worker_queue_tail[worker_id] = NULL;
                }
                worker_queue_head[worker_id] = work_item->next_item;  //NULL if tail
                worker_queue_length[worker_id]--;
                work_item->next_item = NULL;
            }
            pthread_mutex_unlock(&(worker_queue_mutex[worker_id]));
        }
        if (work_item == NULL) continue;

        // Step 2: Perform the work task
        if (client_db.entry[work_item->client_id].connection_closed == false)
        {
            int datafd = client_db.entry[work_item->client_id].datafd;
            // Step 2a: Compute the time out, drop if too old
            struct timeval current_time;
            gettimeofday(&(current_time), NULL);
            time_t s_diff = current_time.tv_sec - work_item->timestamp.tv_sec;
            suseconds_t us_diff = current_time.tv_usec - work_item->timestamp.tv_usec;
            int time_elapsed = s_diff*1000000 + us_diff;
            if (time_elapsed > TIMEOUT_PERIOD)
            {
                printf("Worker %d: %s for client %d dropped due to timeout at %dus\n", 
                        worker_id, work_item->image_name,work_item->client_id, time_elapsed);
                goto Drop;
            }

            // Open image file descriptor to see if file exist
            snprintf(file_path, sizeof(file_path), "./images/%s", work_item->image_name);
            int image_fd = open(file_path, O_RDONLY);
            if (image_fd < 0)
            {
                fprintf(stderr,"Worker %d - ERROR: Cannot open image %s not found!\n", 
                         worker_id, work_item->image_name);
                goto Drop;
            }

            // Step 2b: Send request id
            memset(sendBuff, 0 , strlen(sendBuff));
            snprintf(sendBuff, sizeof(sendBuff), "%d\n", work_item->request_id);
            if (write(datafd, sendBuff, strlen(sendBuff))<0)
                fprintf(stderr,"Worker %d - ERROR: Cannot send request id!\n", worker_id);

            // Step 2c: Send string filename
            memset(sendBuff, 0 , strlen(sendBuff));
            snprintf(sendBuff, sizeof(sendBuff), "%s\n", work_item->image_name);
            if (write(datafd, sendBuff, strlen(sendBuff))<0)
                fprintf(stderr,"Worker %d - ERROR: Cannot send picture name!\n", worker_id);

            //Get image filesize
            struct stat st;
            stat(file_path, &st);
            int fileSize = st.st_size;

            // Step 2d: Send image size information
            memset(sendBuff, 0 , strlen(sendBuff));
            snprintf(sendBuff, sizeof(sendBuff), "%d\n", fileSize);
            if (write(datafd, sendBuff, strlen(sendBuff))<0)
                fprintf(stderr,"Worker %d - ERROR: Cannot send image size!\n", worker_id);

	    // Step 2e: Send the image
            int sent_size = 0;
            if ((sent_size = sendfile(datafd, image_fd, NULL, fileSize)) < 0)
                fprintf(stderr,"Worker %d - ERROR: Cannot send image file!\n", worker_id);
            else
            {
                gettimeofday(&(current_time), NULL);
                s_diff = current_time.tv_sec - work_item->timestamp.tv_sec;
                us_diff = current_time.tv_usec - work_item->timestamp.tv_usec;
                time_elapsed = s_diff*1000000 + us_diff;
                printf("Worker %d: Request %d for %s (%d bytes) sent to client %d in %dus\n", 
                       worker_id, work_item->request_id, work_item->image_name, sent_size, 
                       work_item->client_id, time_elapsed);
            }
            close(image_fd);
        }

        // Step 3: Remove the work item from the client queue        
Drop:   pthread_mutex_lock(&(client_queue_mutex[work_item->client_id]));
        client_queue_head[work_item->client_id] = work_item->next_common;
        if (work_item->next_common == NULL)
        {
            client_queue_tail[work_item->client_id] = NULL;
            client_active_worker[work_item->client_id] = -1; // This worker no longer bound
        }
        client_queue_length[work_item->client_id]--;
        pthread_mutex_unlock(&(client_queue_mutex[work_item->client_id]));

        // Free the work item memory
        if (work_item != NULL) free(work_item);
        work_item = NULL;
    }
Error:
    free(params);
    pthread_exit(NULL);
    return NULL;
}

/***********************************************************
 * CLIENT LIST FUNCTIONS
 ***********************************************************/

/*
 * Initialize the client list structure
 */
void initClientList(client_list_t *client_list)
{
    int i;
    client_thread_entry_t *prev_entry = NULL;
    client_thread_entry_t *current_entry = NULL;

    if (client_list == NULL)
    {
        fprintf(stderr,"ERROR: Input argument is NULL!\n");
        exit(1);
    }
    for (i=0; i<MAX_CLIENTS; ++i)
    {
        current_entry = &(client_list->entry[i]);
        current_entry->client_id = i;
        current_entry->is_empty = true;
        current_entry->client_pid = -1;
        current_entry->ip_addr = 0;
        current_entry->controlfd = -1;
        current_entry->datafd = -1;
        current_entry->connection_closed = true;
        current_entry->prev_entry = prev_entry;
        current_entry->next_entry = NULL;
        if (prev_entry != NULL) prev_entry->next_entry = current_entry;
        prev_entry = current_entry;
    }
    client_list->next_occupied = NULL;
    client_list->next_available = &(client_list->entry[0]);
    return;
}

/*
 * Reserve an entry for incoming client
 * Returns -1 if no entry available
 */
int requestClientEntry(client_list_t *client_list)
{
    client_thread_entry_t *entry = NULL;
    if (client_list == NULL) return -1;

    // Enter criticial section due to list manipulation
    pthread_mutex_lock(&client_db_mutex);
    entry = client_list->next_available;

    // If no more slots are available return -1
    if (entry == NULL)
    {
        pthread_mutex_unlock(&client_db_mutex);
        return -1;
    }
    entry->is_empty = false;

    // Remove the entry from the available list
    if (entry->next_entry != NULL)
    {
        entry->next_entry->prev_entry = NULL;
    }
    client_list->next_available = entry->next_entry;
    if (client_list->next_occupied != NULL)
    {
        client_list->next_occupied->prev_entry = entry;
    }
    entry->next_entry = client_list->next_occupied;
    client_list->next_occupied = entry;    
    num_clients++;
    pthread_mutex_unlock(&client_db_mutex);
 
    return entry->client_id;
}

/*
 * Relinquish a client entry
 */
int relinquishClientEntry(client_list_t *client_list, int client_id)
{
    client_thread_entry_t *entry = NULL;
    if (client_list == NULL || client_id < 0 || client_id > MAX_CLIENTS-1) return -1;
    pthread_mutex_lock(&client_db_mutex);
    entry = &(client_list->entry[client_id]);

    // Nothing to relinquish if already empty
    if (entry->is_empty)
    {
        pthread_mutex_unlock(&client_db_mutex);
        return 0;
    }
    // Return -1 if thread are not cleared properly yet
    if (!(entry->connection_closed))
    {
        pthread_mutex_unlock(&client_db_mutex);
        return -1;
    }
    entry->is_empty = false;
    entry->client_pid = -1;
    entry->ip_addr = 0;
    entry->controlfd = -1;
    entry->datafd = -1;
    entry->connection_closed = true;

    // Put the entry back to list of available entries
    if (entry->next_entry != NULL)
    {
        entry->next_entry->prev_entry = NULL;
    }
    client_list->next_occupied = entry->next_entry;
    if (client_list->next_available != NULL)
    {
        client_list->next_available->prev_entry = entry;
    }
    entry->next_entry = client_list->next_available;
    client_list->next_available = entry;
    num_clients--;
    pthread_mutex_unlock(&client_db_mutex);

    return 0;
}

/***********************************************************
 * DISPATCH FUNCTIONS
 ***********************************************************/

/* 
 * Return the priority class of given panning rate
 */
int findClass(int panning_speed)
{
    if (panning_speed <= 1) return 0;
    if (panning_speed <= 3) return 1;
    if (panning_speed <= 6) return 2;
    if (panning_speed <= 10) return 3;
    return 3;
}

/* 
 * Post a work item to the scheduler and basic admission control
 * Will drop the work right away if the threshold is not met
 * Current rate limited to 3 images per second per client
 */
int postWorkItem(send_work_item_t* work_item)
{
    if (work_item == NULL) return -1;
    int p_rate = panning_speed[work_item->client_id];
    int drop_rate = (MAX_RATE*1000)/p_rate;
    int drop_thres = (drop_rate < 1000)? drop_rate:1000;
    int class_id;
    if (rand()%1000 <= drop_thres)
    {
        if ((class_id = findClass(p_rate)) < 0) goto Error;
        pthread_mutex_lock(&(class_queue_mutex[class_id]));
        if (class_queue_tail[class_id] != NULL)
        {
            class_queue_tail[class_id]->next_item = work_item;
            class_queue_tail[class_id] = work_item;
        }
        else // Queue is empty
        {
            class_queue_head[class_id] = work_item;
            class_queue_tail[class_id] = work_item;
        }
        class_queue_length[class_id]++;
        pthread_mutex_unlock(&(class_queue_mutex[class_id]));
        return 0;
    }
Error:
    return -1;
}

/***********************************************************
 * HELP FUNCTIONS FOR SOCKET READING
 ***********************************************************/

/*
 * Read an integer from socket file descriptor 
 * The raw integer is assumed to be a string terminated by a newline
 */
int readInteger(int fd, int *out)
{
    if (out == NULL) return -1;
    char recvBuff[32], tmpBuff[32];
    int total_bytes_read = 0, buf_len = 0;
    memset(recvBuff, 0, strlen(recvBuff));
    memset(tmpBuff, 0, strlen(tmpBuff));
    while((buf_len = read(fd, recvBuff, 1)) > 0)
    {
        if (recvBuff[0] != '\n')
        {
            tmpBuff[total_bytes_read] = recvBuff[0];
        }
        else break;
        total_bytes_read++;
        // Prevent overflow
        if (total_bytes_read == sizeof(tmpBuff)) return -1;
        memset(recvBuff, 0 , strlen(recvBuff));
    }
    if (buf_len <= 0) return buf_len;
    *out = atoi(tmpBuff);
    return buf_len;
}

/*
 * Read a string from socket file descriptor 
 * The string is terminated by a newline character
 */
int readString(int fd, char *buf, size_t size)
{
    if (buf == NULL) return -1;
    char recvBuff[256];
    memset(recvBuff, 0, strlen(recvBuff));
    int total_bytes_read = 0, buf_len = 0;
    while((buf_len = read(fd, recvBuff, 1)) > 0)
    {
        if (recvBuff[0] != '\n')
        {
            buf[total_bytes_read] = recvBuff[0];
        }
        else break;
        total_bytes_read++;
        // Prevent overflow
        if (total_bytes_read == size) return -1;
        memset(recvBuff, 0 , strlen(recvBuff));
    }
    if (buf_len <= 0) return buf_len;
    return 1;
}


