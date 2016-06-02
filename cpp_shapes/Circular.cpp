#include "Circular.h"

/*
 * Default Constructor: sets radius to 0
 */
Circular::Circular(){
	this->radius = 0;
}

/*
 * Overloaded Constructor: takes in an int radius and sets current instances' data member radius to it
 */
Circular::Circular(int radius){
	this->radius = radius;
}

/*
 * Copy Constructor
 */
Circular::Circular(const Circular &src) {
	this->radius = src.radius;
}

/*
 * Destructor: automatically deallocates memory for radius as it is a native data type
 */
Circular::~Circular() {
}

/*
 * Function diameter(): returns the diameter of the Circular object
 */
int Circular::diameter(){
	return radius * 2;
};

/*
 * Function circumference(): returns the cirumference of the Circular object
 */
double Circular::circumference(){
	return PI * diameter();
};

