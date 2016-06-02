/* 
 * File:   Polygon.h
 * Author: Anton
 *
 */
#include <vector>
#include "Point.h"
#ifndef POLYGON_H
#define	POLYGON_H
using namespace std;

template <class T> class Polygon {
private:
	int numSides;
	vector< Point<T> > points; //vector of Point objects of type T
public:
	Polygon();
	Polygon(int numSides, vector<Point<T> > &points);
	Polygon(const Polygon<T> &orig);
	virtual ~Polygon();
	double perimeter();
	vector<Point<T> > getPoints() const{return points;}; //getter for points
};

#endif	/* POLYGON_H */

