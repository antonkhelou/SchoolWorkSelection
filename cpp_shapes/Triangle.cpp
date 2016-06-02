#include "Triangle.h"
#include <cmath>
#include <iostream>
using namespace std;

/*
 * Default Constructor: sets length to 0
 */
Triangle::Triangle(){
	this->length = 0;
}

/*
 * Overloaded Constructor: sets current instances' length to the length coming into the constructor
 */
Triangle::Triangle(int length){
	this->length = length;
}

/*
 * Copy Constructor
 */
Triangle::Triangle(const Triangle &src) {
	this->length = src.length;
}

/*
 * Destructor
 */
Triangle::~Triangle() {
}

/*
 * Overriden Function perimeter(): calculates the perimeter of the Triangle
 */
double Triangle::perimeter(){
	return (length * 3);//since the triangle is assumed equilateral, perimeter is length * 3
}

/*
 * Overriden Function area(): calculates the area of the Triangle
 */
double Triangle::area(){
	return ((length*length)*(sqrt(3)/4));//based on formula area=l^2*(squareroot(3)/4) where l is length of any side
}

/*
 * Overriden Function print(): prints the contents of the Triangle object.
 */
void Triangle::print(){
	cout << "Triangle: length: " << Triangle::length << ", perimeter: " << Triangle::perimeter() << ", area: " << Triangle::area() << "\n";
}

/*
 * Overriden Function draw(): draws a triangle made of asterisks(*)
 */
void Triangle::draw(){
	for(int i = 0; i <= length; i++){
		for(int j = 0; j < (length-i+1); j++){
			cout << ' ';
		}
		for(int k = 0; k < i; k++){
			cout << "* ";
		}
		cout << '\n';
	}
	cout << '\n';
}
