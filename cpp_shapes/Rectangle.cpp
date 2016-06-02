#include "Rectangle.h"
#include <iostream>
using namespace std;

/*
 * Default Constructor: sets both length and width to 0
 */
Rectangle::Rectangle(){
	this->length = 0;
	this->width = 0;
}

/*
 * Overloaded Constructor: sets current instances' length and width to the length and width
 *							coming into the constructor,respectively.
 */
Rectangle::Rectangle(int length, int width){
	this->length = length;
	this->width = width;
}

/*
 * Copy Constructor
 */
Rectangle::Rectangle(const Rectangle &src) {
	this->length = src.length;
	this->width = src.width;
}

/*
 * Destructor
 */
Rectangle::~Rectangle() {
}

/*
 * Overriden Function perimeter(): calculates the perimeter of the Rectangle
 */
double Rectangle::perimeter(){
	return ((width * 2)+(length * 2));
}

/*
 * Overriden Function area(): calculates the area of the Rectangle
 */
double Rectangle::area(){
	return (width * length );//based on formula area=width*length
}

/*
 * Overriden Function print(): prints the contents of the Rectangle object.
 */
void Rectangle::print(){
	cout << "Rectangle: length: " << Rectangle::length << ", width: " << Rectangle::width << ", perimeter: " << Rectangle::perimeter() << ", area: " << Rectangle::area() << "\n";
}

/*
 * Overriden Function draw(): draws a rectangle made of asterisks(*)
 */
void Rectangle::draw(){
	for(int i = 0; i < width; i++){
		cout << '*';
	}
	cout << "\n";
	for(int i = 0; i < length; i++){
		cout << '*';
		for(int j = 0; j < (width-2); j++){
			cout << ' ';
		}
		cout << "*\n";
	}
	for(int i = 0; i < width; i++){
		cout << '*';
	}
	cout << "\n\n";
}
