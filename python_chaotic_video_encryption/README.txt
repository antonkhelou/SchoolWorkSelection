Usage instructions:

	Dependencies:
	- sudo apt-get install python-opencv
	- sudo apt-get install python-pip
	- pip install numpy

	Usage:
	- python <name-of-demo-file>.py

	Demos:
	- demo-padded.py: single block with padding, nothing interesting
	- demo-largestfit.py: minimizes the amount of cat maps inside the encoded images (i.e. one big block and three small ones)
	- demo-uniformfit.py: creates an even amount of encrypted cat map blocks
	- demo-gray.py: applies grayscale directly onto encrypted signal
	- demo-pqinc.py: probably the most interesting to show, pres N/M keys to control q parameter and </> keys to control the p parameter
