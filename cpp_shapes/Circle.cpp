#include "Circle.h"
#include <iostream>
using namespace std;

/*
 * Default Constructor: sets radius from Circular to 0
 *			(must use setter as radius is private in Ciruclar)
 */
Circle::Circle(){
	this->setRadius(0);
}

/*
 * Overloaded Constructor: sets current instances' radius to the radius coming into the constructor
 */
Circle::Circle(int radius){
	this->setRadius(radius);
}

/*
 * Copy Constructor
 */
Circle::Circle(const Circle &src) {
	this->setRadius(src.getRadius());
}

/*
 * Destructor
 */
Circle::~Circle() {
}

/*
 * Overriden Function print(): prints the contents of the Circle object.
 */
void Circle::print(){
	cout << "Circle: radius: " << this->getRadius() << ", circumference: " << this->circumference() << ", area: " << this->area() << "\n";
}

/*
 * Overriden Function draw(): draws a circle made of asterisks(*)
 */
void Circle::draw(){
	int radius = getRadius();

	for(int i = -radius; i < radius; i++){
		if (i==0){
			cout << " ";
			for(int i=0;i<(radius*2)-1;i++){
				cout << "*";
			}
			cout <<"\n";
		}
		for(int j = -radius; j < radius; j++){
			if(((i*i) + (j*j)) < (radius*radius))//based on the formula x^2+y^2=r^2
				cout << '*';
			else
				cout << ' ';
		}
		cout << "\n";
	}
	cout << "\n";
}

/*
 * Overriden Function area(): calculates the area of the circle
 */
double Circle::area(){
	int rad = getRadius();
	return (rad*rad)*PI;//based on the formula area=r^2*PI
}
