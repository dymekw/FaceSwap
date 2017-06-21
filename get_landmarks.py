import cv2
import dlib
import numpy
from datetime import datetime
import sys
from os.path import basename
import pickle

PREDICTOR_PATH = "shape_predictor_68_face_landmarks.dat"
SERIALIZED_FILE = "static_landmarks.pkl"

detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor(PREDICTOR_PATH)

class TooManyFaces(Exception):
    pass

class NoFaces(Exception):
    pass

def get_landmarks(im):
    rects = detector(im, 1)

    if len(rects) > 1:
        raise TooManyFaces
    if len(rects) == 0:
        raise NoFaces

    return numpy.matrix([[p.x, p.y] for p in predictor(im, rects[0]).parts()])

def read_im_and_landmarks(fname):
    im = cv2.imread(fname, cv2.IMREAD_COLOR)

    s = get_landmarks(im)

    return s

def serialize_landmarks(landmarks):
    output = open(SERIALIZED_FILE, 'wb')
    pickle.dump(landmarks, output)
    output.close()

landmarks1 = read_im_and_landmarks(sys.argv[1])
serialize_landmarks(landmarks1)


