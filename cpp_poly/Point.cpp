/* 
 * File:   Point.cpp
 * Author: Anton
 * 
 */
#include "Point.h"
#include <cmath>
#include <iostream>
using namespace std;

/*
 * Default Constructor: sets x and y to NULL
 */
template <class T> Point<T>::Point() {
	x = NULL;
	y = NULL;
}

/*
 * Overloaded Constructor: sets current instances' x and y to x and y arguments respectively
 */
template <class T> Point<T>::Point(T x ,T y) {
	this->x = x;
	this->y = y;
}

/*
 * Copy Constructor
 */
template <class T> Point<T>::Point(const Point<T> &orig) {
	this->x = orig.x;
	this->y = orig.y;
}

/*
 * Destructor: releases memory allocated for x and y (i.e calls their destructors)
 */
template <class T> Point<T>::~Point() {
	delete &x;
	delete &y;
}

/*
 * Overloaded << operator: Prints the contents of a Point instance in the following
 *							fashio :   (x,y)
 */
template <class T> ostream& operator<<(ostream &out, Point<T> &p){
	out << "(" << p.getx() << "," << p.gety() << ")\n";
	return out;
}

/*
 * Overloaded || operator: This will calculate the Euclidian distance between two points.
 *							It uses sqrt() and pow() functions from cmath to compute Euclidian distance.
 */
template <class T> double operator||(Point<T> &p1, Point<T> &p2){
	return sqrt( pow(p2.getx()-p1.getx(),2) + pow(p2.gety()-p1.gety(),2) );
}