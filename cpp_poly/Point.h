/* 
 * File:   Point.h
 * Author: Anton
 *
 */
#ifndef POINT_H
#define	POINT_H
template <class T> class Point {
private:
	T x;
	T y;
public:
	Point();
	Point(T x, T y);
	Point(const Point<T> &orig);
	virtual ~Point();
	//getters for overloaded operators
	T getx() const{return x;};
	T gety() const{return y;};
};
#endif	/* POINT_H */

