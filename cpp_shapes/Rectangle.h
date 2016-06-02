/* 
 * File:   Rectangle.h
 * Author: Anton
 *
 */

/*
 * Child Class: Inherits from TwoDShape
 */
class Rectangle: public TwoDShape{
private:
	int length;
	int width;
public:
	Rectangle();
	Rectangle(int length, int width);
	~Rectangle();
	Rectangle(const Rectangle &src);

	/*
	 * Overidden functions: print() and draw() coming from Shape.h
	 *						area() and perimeter() coming from TwoDShape.h
	 */
	void print();
	void draw();
	double perimeter();
	double area();
};

