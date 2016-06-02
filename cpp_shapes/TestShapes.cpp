#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <algorithm>
#include "Shape.cpp"
#include "TwoDShape.cpp"
#include "Circular.cpp"
#include "Rectangle.cpp"
#include "Triangle.cpp"
#include "Circle.cpp"
using namespace std;

/*
 * Global variables
 */
int numCircular = 0;
int numNonCircular = 0;

/*
 * Function readShapes(string filename):This function will read the ﬁle, create
 *				the corresponding shape objects, and store them in a vector/array of Shape pointers. This function
 *				checks whether the ﬁle was opened successfully. While the ﬁle does not exist, the function
 *				will prompt the user for another ﬁlename. It will return a vector of Shape pointers.
 */
vector<Shape*> readShapes(string filename){
	ifstream inFile;
	vector<Shape*> ss;

	inFile.open(filename.c_str()); //try to open file
	//while the file is not opened, keep taking input from user
	while(!inFile.is_open()){
		cout << "Error on open. Please Type a new file name:";
		getline(cin, filename,'\n');
		inFile.open(filename.c_str());
	}

	string line;
	string token;

	/*
	 * The following will read line by line the text file. It will then tokenize
	 * that line word by word (seperated by spaces). Depending on what name for a shape
	 * it finds, it will keep tokenizing the words, which are numbers that follow the shape,
	 * until it has all the data required for instantiating an object. At the same time, the
	 * following code will keep track of the number of circular and noncirular object processed.
	 */
	while(getline(inFile,line)){
		istringstream iss(line);
		while (getline(iss, token, ' ')){
			if (token == "Rectangle"){
				getline(iss, token, ' ');
				int length = atoi(token.c_str());
				getline(iss, token, ' ');
				int width = atoi(token.c_str());
				ss.push_back(new Rectangle(length,width));
				numNonCircular++;
			}
			else if(token == "Triangle"){
				getline(iss, token, ' ');
				int length = atoi(token.c_str());
				ss.push_back(new Triangle(length));
				numNonCircular++;
			}
			else if(token == "Circle"){
				getline(iss, token, ' ');
				int radius = atoi(token.c_str());
				ss.push_back(new Circle(radius));
				numCircular++;
			}
			else{
				cout << "Cannot compute " << token << " type..\n";
				throw 1;
			}
		}
	}
	inFile.close();

	return ss;
}

/*
 * This function will be used by the sort() function found in <algorithms>.
 * It will simply do a comparison based on the area of the two Shapes.
 */
bool cmp(Shape* s1,Shape* s2){
	TwoDShape* t1 = (TwoDShape*)s1;
	TwoDShape* t2 = (TwoDShape*)s2;
	return ((t1->area())<(t2->area()));
}

/*
 * This function will will sort the shapes in ascending order using the
 * sort() function found in <algorithms>. It will return the sorted vector when
 * it has finished.
 */
vector<Shape*> sortShapes(vector<Shape*> ss){
	sort(ss.begin(),ss.end(),cmp);
	return ss;
}

int main(){
	string filename;
	cout << "Please Type a file name:";
	getline(cin, filename,'\n');
	vector<Shape*> wtv = readShapes(filename);

	wtv = sortShapes(wtv);

	for(int i=0;i<wtv.size();i++){
		wtv.at(i)->draw();
		wtv.at(i)->print();
	}

	cout << "\n\nNumber of Circular Shapes: " << numCircular << "\n";
	cout << "Number of Non Circular Shapes: " << numNonCircular << "\n\n";
	cout << "Done.";

	return 0;
}