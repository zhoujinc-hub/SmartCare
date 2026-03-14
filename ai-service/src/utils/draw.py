import cv2


def draw_person_box(frame, person):

    x = person["x"]
    y = person["y"]
    w = person["w"]
    h = person["h"]

    cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)