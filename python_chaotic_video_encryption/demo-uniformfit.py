import cv2
import cv2.cv as cv
import numpy
import fractions


def encrypt_video_file_cat_map_stream_max_fit(rounds, p, q, write_file, grayscale, path_to_video_file=None):
	if path_to_video_file is not None:
		video = cv2.VideoCapture(path_to_video_file)
	else:
		video = cv2.VideoCapture(0)

	video.set(cv.CV_CAP_PROP_FRAME_WIDTH, 320)
	video.set(cv.CV_CAP_PROP_FRAME_HEIGHT, 240)

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
		# encrypt the image
		multiple_arnold_chaotic_map_gcd(image, rounds, p, q)

		if grayscale:
			image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

		if write_file:
			writer1.write(image)

		# stream the encrypted image
		cv2.imshow("enc1", image)

		# decrypt the image
		reverse_multiple_arnold_chaotic_map_gcd(image, rounds, p, q, grayscale=grayscale)

		if write_file:
			writer2.write(image)

		# stream the decoded
		cv2.imshow("dec1", image)

		success, image = video.read()

	return (width, height)


def multiple_arnold_chaotic_map_gcd(image_array, rounds, p, q):
	height, width, _ = image_array.shape

	n = fractions.gcd(height, width)

	for i in range(0, width, n):
		for j in range(0, height, n):
			x, y = numpy.meshgrid(range(j,(j+n)), range(i,(i+n)))

			# create the mappings (matrices) for both coordiante matrices
			# (i.e. shuffles the coordinates of the coordinates matrices) 
			xmap = (((1 + p * q) * x + q * y) % n) + j
			ymap = ((p * x + y) % n) + i

			for r in range(0, rounds):
				image_array[x,y] = image_array[xmap,ymap]


def reverse_multiple_arnold_chaotic_map_gcd(image_array, rounds, p, q, grayscale):
	if grayscale:
		height, width = image_array.shape
	else:
		height, width, _ = image_array.shape

	n = fractions.gcd(height, width)

	for i in range(0, width, n):
		for j in range(0, height, n):
			x, y = numpy.meshgrid(range(j,(j+n)), range(i,(i+n)))

			# create the mappings (matrices) for both coordiante matrices
			# (i.e. shuffles the coordinates of the coordinates matrices) 
			xmap = (((1 + p * q) * x + q * y) % n) + j
			ymap = ((p * x + y) % n) + i

			for r in range(0, rounds):
				image_array[xmap,ymap] = image_array[x,y]


if __name__ == "__main__":
    encrypt_video_file_cat_map_stream_max_fit(1, 222, 231, write_file=False, grayscale=False)
