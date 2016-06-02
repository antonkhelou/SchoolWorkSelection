/* 
 * File:   Circular.h
 * Author: Anton
 *
 */

/*
 * Base Class: Circular
 */
class Circular{
private:
	int radius;
public:
	/*
	 * PI is public because it should be accessible to anyone - it is not instance specific
	 *		 const because PI does not change
	 *		 static because there should only be one copy of this variable in memory that should be used
	 *			by all instances of Circular objects - again, it is not instance specific
	 */
	static const double PI = 3.141592;
	Circular();
	Circular(int radius);
	virtual ~Circular();
	Circular(const Circular &src);
	int getRadius() const{return radius;};
	void setRadius(int radius){this->radius = radius;};
	int diameter();
	double circumference();
};

