#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <arpa/inet.h>
#include <sys/stat.h>
#include <time.h>

#include <pthread.h>

#define CONTROL_PORT		5003
#define DATA_PORT		5004
#define TIMEOUT_PERIOD		1000000

/***********************************************************
 * THREAD FUNCTION FORWARD DECLARATION
 ***********************************************************/

void* data_output(void* arg);	// Sender thread
void* data_input(void* arg);	// Receiver thread

/***********************************************************
 * Global Variables
 ***********************************************************/

int controlfd = 0;		// Control socket file descriptor
int datafd = 0;			// Data socket file descriptor
char folderPath[32];		// Folder path string
int img_requests_per_sec;	// Panning rate

// Log list
struct logList
{
    int request_id;		// ID to identify request
    char fileName[256];		// Image name
    int timeStamp;		// timestamp in microsecond
    struct logList *nextLog;	// next element in log
};
struct logList *logHead = NULL;	// Head of structure
pthread_mutex_t logList_mutex;	// Mutex for logHead

/***********************************************************
 * MAIN THREAD
 ***********************************************************/

int main(int argc, char *argv[]){
    char sendBuff[1024];
    char recvBuff[80000], tmpBuff[32], fullPath[32];
    int buf_len = 0;
    int recv_pid = 0;
    int total_bytes_read = 0;
    struct sockaddr_in ctl_addr, data_addr;
    int pid = getpid();

    // Generate pseudo-random number seed
    struct timeval t1;
    gettimeofday(&t1, NULL);
    srand(t1.tv_usec * t1.tv_sec);

    // Fixed panning speed for given client
    img_requests_per_sec = (rand() % 10) + 1;

    // Check for argument usage
    if(argc < 2)
    {
        printf("\n Usage: %s <ip of server> \n",argv[0]);
        return 1;
    }

    // Initialize buffer memory
    memset(recvBuff, '0',sizeof(recvBuff));

    // Create data socket
    datafd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (datafd < 0)
    {
        fprintf(stderr,"ERROR: Cannot create data socket!\n");
        goto Error;
    }

    // Data socket address/port
    memset(&data_addr, '0', sizeof(data_addr));
    if(inet_pton(AF_INET, argv[1], &data_addr.sin_addr)<=0)
    {
        fprintf(stderr,"ERROR: inet_pton error occured\n");
        goto Error;
    }
    data_addr.sin_family = AF_INET;
    data_addr.sin_port = htons(DATA_PORT);
   
    // Create control socket
    controlfd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (controlfd < 0)
    {
        fprintf(stderr,"ERROR: Cannot create control socket!\n");
        goto Error;
    }

    // Control socket address/port
    memset(&ctl_addr, '0', sizeof(ctl_addr));
    if(inet_pton(AF_INET, argv[1], &ctl_addr.sin_addr)<=0)
    {
        fprintf(stderr,"ERROR: inet_pton error occured\n");
        goto Error;
    }
    ctl_addr.sin_family = AF_INET;
    ctl_addr.sin_port = htons(CONTROL_PORT); 

    // Make a new folder to store the clients images
    sprintf(folderPath, "downloadedImages/%i", pid);
    mkdir(folderPath, S_IRWXU);

    // Step 1a: Connect to control port
    if(connect(controlfd, (struct sockaddr *)&ctl_addr, sizeof(ctl_addr)) < 0)
    {
       fprintf(stderr,"ERROR: Control Connection Failed\n");
       goto Error;
    }

    // Step 1b: Send control port PID message
    memset(sendBuff, 0 , strlen(sendBuff));
    snprintf(sendBuff, sizeof(sendBuff), "%i\n", getpid());
    if (write(controlfd, sendBuff, strlen(sendBuff))<0)
        fprintf(stderr,"ERROR: Cannot send PID to control port!\n");
    
    // Step 1c: Wait for control PID echo
    memset(recvBuff, 0 , strlen(recvBuff));
    memset(tmpBuff, 0 , strlen(tmpBuff));
    total_bytes_read = 0;
    while((buf_len = read(controlfd, recvBuff, 1)) > 0)
    {
        if (recvBuff[0] != '\n')
        {
            tmpBuff[total_bytes_read] = recvBuff[0];
        }
        else break;
        total_bytes_read++;
        if (total_bytes_read == sizeof(tmpBuff)) goto Error;
        memset(recvBuff, 0 , strlen(recvBuff));
    }
    if (buf_len <= 0) goto Error;
    recv_pid = atoi(tmpBuff);
    if (recv_pid == getpid())
        printf("Control connection reply received: %d\n",recv_pid);
    else
    {
        fprintf(stderr,"ERROR: Wrong control connection PID received!\n");
        goto Error;
    }

    // Step 2a: Connect to data port
    if(connect(datafd, (struct sockaddr *)&data_addr, sizeof(data_addr)) < 0)
    {
       fprintf(stderr,"ERROR: Data Connection Failed\n");
       goto Error;
    }

    // Step 2b: Send data port PID message
    memset(sendBuff, 0 , strlen(sendBuff));
    snprintf(sendBuff, sizeof(sendBuff), "%i\n", getpid());
    if (write(datafd, sendBuff, strlen(sendBuff))<0)
        fprintf(stderr,"ERROR: Cannot send PID to data port!\n");

    // Step 2c: Wait for data connection PID echo
    memset(recvBuff, 0 , strlen(recvBuff));
    memset(tmpBuff, 0 , strlen(tmpBuff));
    total_bytes_read = 0;
    while((buf_len = read(datafd, recvBuff, 1)) > 0)
    {
        if (recvBuff[0] != '\n')
        {
            tmpBuff[total_bytes_read] = recvBuff[0];
        }
        else break;
        total_bytes_read++;
        if (total_bytes_read == sizeof(tmpBuff)) goto Error;
        memset(recvBuff, 0 , strlen(recvBuff));
    }
    if (buf_len <= 0) goto Error;
    recv_pid = atoi(tmpBuff);
    if (recv_pid == getpid())
        printf("Data connection reply received: %d\n",recv_pid);
    else
    {
        fprintf(stderr,"ERROR: Wrong data connection PID received!\n");
        goto Error;
    }

    // Reset buffer and buffer length
    memset(recvBuff, 0 , strlen(recvBuff));
    buf_len = 0;

    // Prepare log file header
    FILE *logFile;
    memset(fullPath, 0, strlen(fullPath));
    sprintf(fullPath, "%s/log_%d.txt", folderPath, getpid());
    logFile = fopen(fullPath,"a+");
    fprintf(logFile, "Client PID: %d\tPanning rate: %d\n",getpid(),img_requests_per_sec);
    fprintf(logFile, "ID\tName\t\tTime\tValid\tOld\n");
    fclose(logFile);

    // Two-thread implementation
    pthread_t data_output_thread;
    pthread_t data_input_thread;

    // Launch receiver thread
    if (pthread_create(&data_input_thread, NULL, data_input, NULL) != 0)
    {
        fprintf(stderr,"ERROR: Cannot create data_input_thread!\n");                
        close(controlfd);
    }
    // Launch sender thread
    if (pthread_create(&data_output_thread, NULL, data_output, NULL) != 0)
    {
        fprintf(stderr,"ERROR: Cannot create data_output_thread!\n");                
        close(datafd);
    }

    // Main thread goes to sleep
    while(1);
    return 0;
Error:
    close(controlfd);
    close(datafd);
    exit(1);
    return 1;
}

/***********************************************************
 * SENDER THREAD
 ***********************************************************/
/*
 * Thread that sends both control messages and image request
 */
void* data_output(void* arg)
{
    char sendBuff[1024];
    int imageNum = 0;
    int request_id = 0;

    printf("Client PID %d : panning speed = %d/s\n", getpid(), img_requests_per_sec);

    while(1)
    {
        int sleep_duration_in_micro = (1.0 / (float) img_requests_per_sec) * 1000000;

        //printf("sleep_duration_in_micro: %d\n", sleep_duration_in_micro);

        // Send how many images this client scrolls in a second
        memset(sendBuff, 0 , strlen(sendBuff)); //reset buffer
        snprintf(sendBuff, sizeof(sendBuff), "%d\n", img_requests_per_sec);
        if (write(controlfd, sendBuff, strlen(sendBuff))<0)
        {
            fprintf(stderr,"ERROR: Cannot send panning speed!\n");
            pthread_exit(NULL);
        }

        int i;
        // Send image requests
        for(i=0; i<img_requests_per_sec; i++)
        {
            imageNum = rand() % 100;

            // Step 1: Send request ID
            memset(sendBuff, 0 , strlen(sendBuff));
            snprintf(sendBuff, sizeof(sendBuff), "%i\n", request_id);
            if (write(datafd, sendBuff, strlen(sendBuff))<0)
                fprintf(stderr,"ERROR: Cannot send request ID to data port!\n");

            // Step 2: Send image name request
            memset(sendBuff, 0 , sizeof(sendBuff)); //reset buffer
            snprintf(sendBuff, sizeof(sendBuff), "img%d.png\n", imageNum);
            if (write(datafd, sendBuff, strlen(sendBuff))<0)
            {
                fprintf(stderr,"ERROR: Cannot send image request!\n");
                pthread_exit(NULL);
            }

            // Log the request
            struct logList *newLog = (struct logList*)malloc(sizeof(struct logList));
            struct timeval current_time;
            gettimeofday(&(current_time), NULL);
            time_t s = current_time.tv_sec;
            suseconds_t us = current_time.tv_usec;
            newLog->timeStamp = s*1000000 + us;
            sprintf(newLog->fileName, "img%d.png", imageNum);
            newLog->request_id = request_id;
            // Store in the global log list
            pthread_mutex_lock(&logList_mutex);
            newLog->nextLog = logHead;
            logHead = newLog;
            pthread_mutex_unlock(&logList_mutex);

            request_id++;
            usleep(sleep_duration_in_micro);
        }
    }
}

/***********************************************************
 * RECEIVER THREAD
 ***********************************************************/
/*
 * Thread that receives images from data socket
 */
void* data_input(void* arg)
{
    int buf_len = 0, image_size = 0, request_id = 0;
    char sendBuff[1024], recvBuff[500000];
    char imgBuff[500000];
    char fileName[256], fullPath[512];
    char image_size_str[32], request_id_str[32];
    int total_bytes_read = 0, image_bytes_left = 0;
    char *buf_pos = NULL;
    int client_pid = getpid();
    int total_number_received = 0, total_old = 0;

    FILE *logFile;
    memset(fullPath, 0, strlen(fullPath));
    sprintf(fullPath, "%s/log_%d.txt", folderPath, getpid());
    logFile = fopen(fullPath,"a+");

    while(1)
    {
        // Step 1: Wait for request ID
        memset(recvBuff, 0, strlen(recvBuff));
        memset(request_id_str, 0, strlen(request_id_str));
        total_bytes_read = 0;
        while((buf_len = read(datafd, recvBuff, 1)) > 0)
        {
            if (recvBuff[0] != '\n')
            {
                request_id_str[total_bytes_read] = recvBuff[0];
            }
            else break;
            total_bytes_read++;
            memset(recvBuff, 0 , strlen(recvBuff));
        }
        if (buf_len <= 0)
        {
            fprintf(stderr, "Client PID %d ERROR: Cannot get request ID!\n", client_pid);
            goto End;
        }
        request_id = atoi(request_id_str);

        // Step 2: Wait for filename
        memset(fileName, 0, sizeof(fileName));
        memset(recvBuff, 0, strlen(recvBuff));
        total_bytes_read = 0;
        while((buf_len = read(datafd, recvBuff, 1)) > 0)
        {
            if (recvBuff[0] != '\n')
            {
                fileName[total_bytes_read] = recvBuff[0];
            }
            else break;
            total_bytes_read++;
            if (total_bytes_read == sizeof(fileName))
            {
                fprintf(stderr, "Client PID %d ERROR: Filename over 256!\n", client_pid);
                break;
            }
            memset(recvBuff, 0 , strlen(recvBuff));
        }
        if (buf_len <= 0)
        {
            fprintf(stderr, "Client PID %d ERROR: Cannot get filename!\n", client_pid);
            goto End;
        }        

        // Step 3: Wait for image size
        memset(recvBuff, 0, strlen(recvBuff));
        memset(image_size_str, 0, strlen(image_size_str));
        total_bytes_read = 0;
        while((buf_len = read(datafd, recvBuff, 1)) > 0)
        {
            if (recvBuff[0] != '\n')
            {
                image_size_str[total_bytes_read] = recvBuff[0];
            }
            else break;
            total_bytes_read++;
            memset(recvBuff, 0 , strlen(recvBuff));
        }
        if (buf_len <= 0)
        {
            fprintf(stderr, "Client PID %d ERROR: Cannot get image size!\n", client_pid);
            goto End;
        }
        image_size = atoi(image_size_str);        

        // Step 4: Get image data
        memset(recvBuff, 0, strlen(recvBuff));
        memset(imgBuff, 0, strlen(recvBuff));
        image_bytes_left = image_size;
        buf_pos = imgBuff;
        while((buf_len = read(datafd, recvBuff, image_bytes_left)) > 0)
        {
            //printf("%d\n",buf_len);
            memcpy(buf_pos, recvBuff, buf_len);
            if (buf_len == image_bytes_left) break;
            image_bytes_left = image_bytes_left - buf_len;
            buf_pos = buf_pos + buf_len;
        }
        if (buf_len <= 0)
        {
            fprintf(stderr, "Client PID %d ERROR: Cannot get image!\n", client_pid);
            goto End;
        }

        // Write to image file
        FILE *file;
        memset(fullPath, 0, strlen(fullPath));
        sprintf(fullPath, "%s/%s", folderPath, fileName);
        file = fopen(fullPath,"w");
        fwrite(imgBuff, 1, image_size, file);
        fclose(file);

        //printf("Client PID %d received %s (%d bytes)\n", client_pid, fileName, image_size);

        // Get the current time
        // Note that this time is "frozen" since images can be received in this period
        struct timeval current_time;
        gettimeofday(&(current_time), NULL);
        time_t s = current_time.tv_sec;
        suseconds_t us = current_time.tv_usec;
        int timeNow = s*1000000 + us;

        // Write to log
        pthread_mutex_lock(&logList_mutex);
        struct logList *ptr = logHead;
        struct logList *previous = NULL;
        // Check to see where the file request was sent
        while(ptr != NULL && ptr->request_id != request_id)
        {
            previous = ptr;
            ptr = ptr->nextLog;
        }
        // There should always be a matching request for a reply
        if(ptr != NULL)
        {
            //Find the time difference between sent and received
            int timeToReceive = timeNow - ptr->timeStamp; 
            total_number_received++;
            //Save to log
            fprintf(logFile, "%d\t%s\t%i\t%d\t%d\n", ptr->request_id, ptr->fileName, 
                    timeToReceive, total_number_received, total_old);
            fflush(logFile);

            if(previous != NULL)
            {
                if(ptr->nextLog != NULL)
                    previous->nextLog = ptr->nextLog;
                else 
                    previous->nextLog = NULL;
                free(ptr);
            }
            else // ptr is actually the head
            {
                logHead = ptr->nextLog;
                free(ptr);
            }
        }
        else // This is a request that is already timed out
        {
            total_old++;
            fprintf(logFile, "%d\t%s\told\t%d\t%d\n", request_id, fileName, 
                    total_number_received, total_old);
            fflush(logFile);
        }
        pthread_mutex_unlock(&logList_mutex);

        // Check if any images have timed out, after TIMEOUT_PERIOD
        // Note: use the "frozen" current time previously obtained
        pthread_mutex_lock(&logList_mutex);
        struct logList *ctr = logHead;
        struct logList *prev = NULL;
        // Assume the logList is in order of timestamp (which it should be)
        while(ctr != NULL && (ctr->timeStamp-timeNow) > TIMEOUT_PERIOD)
        {
            prev = ctr;
            ctr = ctr->nextLog;
        }
        // Delete all the old entries after that point
        while(ctr != NULL)
        {
            fprintf(logFile, "%d\t%s\tout\t%d\t%d\n", ctr->request_id, ctr->fileName, 
                    total_number_received, total_old);
            fflush(logFile);
            if(prev != NULL)
            {
                if(ctr->nextLog != NULL)
                {
                    prev->nextLog = ctr->nextLog;
                    free(ctr);
                    ctr = prev->nextLog;
                }
                else
                {
                    prev->nextLog = NULL;
                    free(ctr);
                    ctr = NULL;
                }
                
            }
            else // the matching entry is the head
            {
                logHead = ctr->nextLog;
                free(ctr);
                ctr = logHead;
            }
        }
        pthread_mutex_unlock(&logList_mutex);
    }
End:
    printf("Client PID %d Connection closed\n", client_pid);
    close(datafd);
    fclose(logFile);
    pthread_exit(NULL);
    return NULL;
}

