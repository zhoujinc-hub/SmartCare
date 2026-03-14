import cv2


def draw_person(frame, person, fall_condition, ratio):

    x = person["x"]
    y = person["y"]
    w = person["w"]
    h = person["h"]

    if fall_condition:
        color = (0, 0, 255)
        label = f"FALL r:{ratio:.2f}"
    else:
        color = (0, 255, 0)
        label = f"Person r:{ratio:.2f}"

    cv2.rectangle(frame, (x, y), (x+w, y+h), color, 2)

    cv2.putText(
        frame,
        label,
        (x, y-10),
        cv2.FONT_HERSHEY_SIMPLEX,
        0.6,
        color,
        2
    )