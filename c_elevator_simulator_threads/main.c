#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
void *clockRoutine(void *arg);
void *personRoutine(void *arg);
int request(int currentFloor, int destinationFloor, int personID);

typedef struct{
    int direction, busy, numPeople, numMaxPeople, numFloors, currFloor;
    pthread_mutex_t mutex;
} elevator_t;

//these condition variable arrays will be used to notify people
//that are requesting for a floor (i.e waiting at the floor) and
//for people reaching their destination floor (i.e waiting inside elevator)
pthread_cond_t *requestCond, *destinationCond;

elevator_t elevator;

//the following three variables are used to store the command line
//arguments
int numPeopleInSim;
int numFloorsInSim;
int numPeopleCapElev;

int timeInSeconds;
int peopleCounter = 0;

//these variables are used to ensure that the elevators stops where there
//are no people in the elevator and no people in the waiting for the elevator.
int numPeopleWaiting = 0;
int isRequestPresent = 0;

//the two variables are used to keep the people's IDs synched
//and meaningfully increment them
int peopleIDCounter = 0;
pthread_mutex_t peopleMutex;

//these two variables are used to control when the elevator decides to
//stop sweeping a direction.
int highestReqFloor = -1;
int lowestReqFloor = 9999;


int main(int argc, char *argv[]){
    //this code needs three arguments to run. First is the number
    //of people in the building. Second is the number of floors and third
    //is the elevators max capacity
    int argCounter = 0;
    while(argc>1){
        switch(argCounter){
            case 0:{
                numPeopleInSim=atoi(argv[1]);
                if(numPeopleInSim==0){
                    numPeopleInSim = 5; //default
                }
                break;
            }
            case 1:{
                numFloorsInSim=atoi(argv[1]);
                if(numFloorsInSim==0){
                    numFloorsInSim = 10; //default
                }
                break;
            }
            case 2:{
                numPeopleCapElev=atoi(argv[1]);
                if(numPeopleCapElev==0){
                    numPeopleCapElev = 8; //default
                }
                break;
            }
        }
        argv++;
        argc--;
        argCounter++;
    }
    printf("numPeopleInSim: %d\n", numPeopleInSim);
    printf("numFloorsnSim: %d\n", numFloorsInSim);
    printf("numPeopleCapElev: %d\n", numPeopleCapElev);

    pthread_t clockThread;
    pthread_t people[numPeopleInSim];

    //initialize the elevator struct
    elevator.direction = 1;
    elevator.busy = 0;
    elevator.numPeople = 0;
    elevator.numMaxPeople = numPeopleCapElev;
    elevator.numFloors = numFloorsInSim;
    elevator.currFloor = 0;
    pthread_mutex_init(&elevator.mutex, NULL);

    //initialize condition variables
    requestCond = (pthread_cond_t*) malloc(numFloorsInSim * sizeof(pthread_cond_t));
    destinationCond = (pthread_cond_t*) malloc(numFloorsInSim * sizeof(pthread_cond_t));
    int i;
    for(i=0;i<numFloorsInSim;i++){
        pthread_cond_init(&requestCond[i], NULL);
        pthread_cond_init(&destinationCond[i], NULL);
    }

    //create the people threads
    for(i=0;i<numPeopleInSim;i++){
        people[i] = malloc(100);
        pthread_create(&people[i], NULL, personRoutine, NULL);
    }

    //create clock thread
    pthread_create(&clockThread, NULL, clockRoutine, NULL);

    //pthread_join(people[0],NULL);
    pthread_join(clockThread,NULL);
    //return 0;
}

//this method is called by the people threads for elevator requests
int request(int currentFloor, int destinationFloor, int personID){
    pthread_mutex_lock(&elevator.mutex);
        int success = 1; //return variable

        //the following statements are used to ensure that the elevator stops its sweeps.
        //highestReqFloor will be set to -1 at first, and will constantly be overridden
        //whenever a person thread with a higherFloor requests the elevator.
        //The same goes for lowestReqFloor, but the other way around.
        if(currentFloor>=elevator.currFloor && currentFloor > highestReqFloor){
            highestReqFloor = currentFloor;
        }
        if(currentFloor<=elevator.currFloor && currentFloor < lowestReqFloor){
            lowestReqFloor = currentFloor;
        }

        numPeopleWaiting++;
    pthread_cond_wait(&requestCond[currentFloor],&elevator.mutex);//wait on your floor
        numPeopleWaiting--;

        if(((destinationFloor-currentFloor)<0 && elevator.direction == 1)||
           ((destinationFloor-currentFloor)>0 && elevator.direction == -1)){
            success = -1;
            printf("I am person %d and the elevator is not goint my direction. Trying again..\n",personID);
        }
        else if(elevator.numPeople == elevator.numMaxPeople){
            success = -1;
            printf("I am person %d and the elevator is full. Trying again..\n",personID);
        }
        else{
                //the same variable highestReqFloor is used for destination requests as well to ensure
                //the elevator stops sweeping if no higher destination requests than a certain floor.
                //Same goes for the lowestReqFloor.
                if(destinationFloor>elevator.currFloor && destinationFloor > highestReqFloor){
                    highestReqFloor = destinationFloor;
                }
                if(destinationFloor<elevator.currFloor && destinationFloor < lowestReqFloor){
                    lowestReqFloor = destinationFloor;
                }
                isRequestPresent = 1;
                elevator.numPeople = elevator.numPeople + 1;
                printf("I am person %d and I am on the elevator.\n",personID);

            pthread_cond_wait(&destinationCond[destinationFloor],&elevator.mutex);//wait until you reach your floor

                elevator.numPeople = elevator.numPeople - 1;

                //the following is to ensure that the elevator stops when there are no people waiting for it and
                //no people inside.
                if(elevator.numPeople==0 && numPeopleWaiting==0){
                    isRequestPresent = 0;
                }
        }

    pthread_mutex_unlock(&elevator.mutex);
    return success;
}

//this method is called by the clock thread at every second.
void elevatorClockTick(){
    pthread_mutex_lock(&elevator.mutex);

    if(isRequestPresent==1){
        if(elevator.direction == 1){//going up
            if(elevator.currFloor == elevator.numFloors){//if youre on the last floor
                elevator.currFloor = elevator.currFloor - 1;
            }
            else{
                elevator.currFloor = elevator.currFloor + 1;
                //the following checks if the elevator has reached the top floor of the building or
                //if the elevator has reached the highestReqFloor, if he has, change the direction
                //and reset highestReqFloor to -1.
                if(elevator.currFloor == elevator.numFloors - 1 || highestReqFloor==elevator.currFloor){
                    elevator.direction = -1;
                    highestReqFloor= -1;
                }
            }
        }
        else if(elevator.direction == -1){//going down
            if(elevator.currFloor == 0){//if youre on the first floor
                elevator.currFloor = elevator.currFloor + 1;
            }
            else{
                elevator.currFloor = elevator.currFloor - 1;
                //the following checks if the elevator has reached the lowest floor of the building or
                //if the elevator has reached the lowestReqFloor, if he has, change the direction
                //and reset lowestReqFloor to 9999.
                if(elevator.currFloor == 0 || lowestReqFloor == elevator.currFloor){
                    elevator.direction = 1;
                    lowestReqFloor = 9999;//reset lowestReqFloor to a v large number
                }
            }
        }
    }
    printf("\nfloor: %d\n",elevator.currFloor);
    pthread_cond_broadcast(&requestCond[elevator.currFloor]);
    pthread_cond_broadcast(&destinationCond[elevator.currFloor]);

    pthread_mutex_unlock(&elevator.mutex);
}



void *personRoutine(void *arg){
    pthread_mutex_lock(&peopleMutex);
    peopleIDCounter++;
    int personID = peopleIDCounter;
    pthread_mutex_unlock(&peopleMutex);

    int floor = 0;
    int workTime, destFloor, result;
    while(timeInSeconds<100){
	if(result!=-1){//-1 is returned if the elevator was full or not going the same direction - i.e dont generate a new floor.
		destFloor = rand() % numFloorsInSim;//generate a number between 0 and numFloorsInSim
		//the following while is to make sure the randomly generated destination floor is not
		//the same as the current floor of the person.
		while(destFloor==floor){
		    destFloor = rand() % numFloorsInSim;
		}
		printf("I am person %d. I am on floor %d and going to %d.\n", personID, floor,destFloor);
	}
        result = request(floor, destFloor,personID);
        if(result==1){
            floor = destFloor;//request was fulfilled, the person has reached the floor he requested.
            printf("I am person %d. I have arrived to floor %d.\n", personID, floor);

            workTime = rand() % 8;//random int value from 0 to 8 seconds
            sleep(workTime); //simulate that he is working on that floor
        }
    }
}

void *clockRoutine(void *arg){
    for(timeInSeconds=0;timeInSeconds<100;timeInSeconds++){
        elevatorClockTick();
        sleep(1);//sleep 1 sec
    }
    printf("Suddenly, the elevators' piston made a screeching metalic noise.\n");
    printf("The lights went out. You feel your feet take off from the floor. Nothing more.\n");
    printf("The simulation is over.\n");
}
