/* 
 * File:   TwoDShape.h
 * Author: Anton
 *
 */

/*
 * Abstract Class TwoDShape: because it inherits draw() and print() from Shape.h and does not
 *								provide an implementation for them.
 */
class TwoDShape: public Shape{
public:
	TwoDShape();
	virtual ~TwoDShape();
	/*
	 * Virtual function perimeter() because we don't want
	 * to force child classes to implement these (i.e Circle should not have a
	 * perimeter  but rather a circumference)
	 */
	virtual double perimeter(){};
	
	/*
	 * Pure virtual because all 2D shapes should have a specific area implementation.
	 */
	virtual double area()=0;
};