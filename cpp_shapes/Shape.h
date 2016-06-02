/* 
 * File:   Shape.h
 * Author: Anton
 *
 */

/*
 * Abstract Base Class: Shape
 */
class Shape{
public:
	Shape();
	virtual ~Shape();
	/*
	 * Pure virtual function print() and draw() as we want
	 * to force child classes to implement them.
	 */
	virtual void print()=0;
	virtual void draw()=0;
};

