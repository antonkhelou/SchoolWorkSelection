/* 
 * File:   main.cpp
 * Author: Anton
 *
 */
#include "Point.cpp"
#include "Polygon.cpp"
#include <vector>
#include <iostream>
using namespace std;

int main() {
	vector< Point<int> > vec;
	Point<int> i(2,3);
	Point<int> j(4,6);
	Point<int> k(8,12);
	vec.push_back(i);
	vec.push_back(j);
	vec.push_back(k);
	Polygon<int> pol(3,vec);

	cout << pol.perimeter() << "\n";
	//cout << i << j << k;
	cout << pol;
	return 0;
}

