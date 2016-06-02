/* 
 * File:   Polygon.cpp
 * Author: Anton
 * 
 */
#include "Polygon.h"
#include <iostream>
using namespace std;

/*
 * Default Constructor: sets numSides to 0 and points to NULL
 */
template <class T> Polygon<T>::Polygon() {
	numSides = 0;
	points = NULL;
}

/*
 * Overloaded Constructor: takes in numSides and points and sets current instances' data member numSides
 *							and points to those, respectively.
 */
template <class T> Polygon<T>::Polygon(int numSides, vector< Point<T> > &points){
	this->numSides = numSides;
	this->points = points;
}

/*
 * Copy Constructor
 */
template <class T> Polygon<T>::Polygon(const Polygon<T> &orig) {
	this->numSides = orig.numSides;
	this->points = orig.points;
}

/*
 * Destructor
 */
template <class T> Polygon<T>::~Polygon() {
	//vector will clean up the space it has allocated to store the objects
	//because I don't instantiate the objects with new, then freeing the memory will be automatic.
}

/*
 * Function perimeter: Calculates the perimeter of based on the points data member. It will calculate
 *						the perimeter using the overloaded || operator found in Point.cpp.
 */
template <class T> double Polygon<T>::perimeter(){
	double perim = 0.0;
	for(int i=0;i<(points.size()-1);i++){
		perim += points.at(i) || points.at(i+1);
	}
	return perim;
}

/*
 * Overloaded << operator: Prints all the points using the overloaded << operator found in Point.cpp
 */
template <class T> ostream& operator<<(ostream &out, Polygon<T> &p){
	vector< Point<T> > pv = p.getPoints();
	for(int i=0;i<pv.size();i++){
		out << pv.at(i);
	}
	return out;
}