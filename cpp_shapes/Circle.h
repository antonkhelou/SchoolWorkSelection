/* 
 * File:   Circle.h
 * Author: Anton
 *
 */

/*
 * Child Class: Inherits from TwoDShape and Circular
 */
class Circle: public TwoDShape, public Circular{
public:
	Circle();
	Circle(int radius);
	~Circle();
	Circle(const Circle &src);

	/*
	 * Overidden functions: print() and draw() coming from Shape.h
	 *						area() coming from TwoDShape.h
	 */
	void print();
	void draw();
	double area();
};

