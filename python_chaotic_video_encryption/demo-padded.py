import cv2
import cv2.cv as cv
import numpy
import fractions


def encrypt_video_file_cat_map_stream_max_fit(rounds, p, q, write_file, grayscale, path_to_video_file=None):
	if path_to_video_file is not None:
		video = cv2.VideoCapture(path_to_video_file)
	else:
		video = cv2.VideoCapture(0)

	success, image = video.read()

	width, height, depth = image.shape

	# find the largest dimension
	max_dimension = max(height, width)

	cv2.startWindowThread()
	cv2.namedWindow("enc1", cv.CV_WINDOW_AUTOSIZE)
	cv2.namedWindow("dec1", cv.CV_WINDOW_AUTOSIZE)

	if write_file:
		fps = 8
		writer1 = cv2.VideoWriter('enc1.avi', cv.CV_FOURCC('M', 'J', 'P', 'G'), fps, (max_dimension, max_dimension))
		writer2 = cv2.VideoWriter('dec1.avi', cv.CV_FOURCC('M', 'J', 'P', 'G'), fps, (height, width))

	while success:
		# resize the image array to the largest dimension using black pixels
		image.resize(max_dimension, max_dimension, depth)
		# encrypt the image
		arnold_chaotic_map(image, rounds, p, q)

		if grayscale:
			image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

		if write_file:
			writer1.write(image)

		# stream the encrypted image
		cv2.imshow("enc1", image)

		# decrypt the image
		reverse_arnold_chaotic_map(image, rounds, p, q)

		# resize to the original size
		image.resize((width, height, depth))

		if write_file:
			writer2.write(image)

		# stream the decoded
		cv2.imshow("dec1", image)

		success, image = video.read()

	return (width, height)


def arnold_chaotic_map(image_array, rounds, p, q):
	height, width, _ = image_array.shape

	n = width

	# get the coordinates matrices for the x and y axies
	x, y = numpy.meshgrid(range(n), range(n))

	# create the mappings (matrices) for both coordiante matrices
	# (i.e. shuffles the coordinates of the coordinates matrices) 
	xmap = ((1 + p * q) * x + q * y) % n
	ymap = (p * x + y) % n

	# apply the actual shuffling using for round times
	for r in range(0, rounds):
		image_array[x,y] = image_array[xmap,ymap]


def reverse_arnold_chaotic_map(image_array, rounds, p, q, grayscale=False):
	if grayscale:
		height, width = image_array.shape
	else:
		height, width, _ = image_array.shape

	n = width

	x, y = numpy.meshgrid(range(n), range(n))
	xmap = ((1 + p * q) * x + q * y) % n
	ymap = (p * x + y) % n

	for r in range(0, rounds):
		image_array[xmap,ymap] = image_array[x,y]


if __name__ == "__main__":
    encrypt_video_file_cat_map_stream_max_fit(1, 222, 231, write_file=False, grayscale=False)
