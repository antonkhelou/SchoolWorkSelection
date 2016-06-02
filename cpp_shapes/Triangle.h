/* 
 * File:   Triangle.h
 * Author: Anton
 *
 */

/*
 * Child Class: Inherits from TwoDShape
 */
class Triangle: public TwoDShape{
private:
	int length;
public:
	Triangle();
	Triangle(int length);
	~Triangle();
	Triangle(const Triangle &src);

	/*
	 * Overidden functions: print() and draw() coming from Shape.h
	 *						area() and perimeter() coming from TwoDShape.h
	 */
	void print();
	void draw();
	double perimeter();
	double area();
};

