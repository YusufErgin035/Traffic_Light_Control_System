package com.example.trafficlightcontrolsystem;

import javafx.animation.PathTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

public class Car {
    Rectangle shape;
    double speed;

    public Car(double x, double y, Color color) {
        shape = new Rectangle(30, 15, color);
        shape.setLayoutX(x);
        shape.setLayoutY(y);
        speed = 2;
    }

    public Rectangle getShape() {
        return shape;
    }

    void moveX() {
        shape.setTranslateX(shape.getTranslateX() + speed);
    }
    void moveY() {
        shape.setTranslateY(shape.getTranslateY() + speed);
    }

    double getCurrentX() {
        return shape.getLayoutX() + shape.getTranslateX();
    }

    public void arcTurn(boolean clockwise) {
        // Yarıçap ve merkez konum
        double radius = 40;
        double centerX = shape.getLayoutX() + (clockwise ? radius : -radius);
        double centerY = shape.getLayoutY();

        // Başlangıç ve bitiş açıları
        double startAngle = clockwise ? 180 : 0;
        double endAngle = clockwise ? 270 : 90;

        Arc arc = new Arc(centerX, centerY, radius, radius, startAngle, 90);
        arc.setType(ArcType.OPEN);

        Path path = new Path();
        path.getElements().add(new MoveTo(shape.getLayoutX(), shape.getLayoutY()));
        path.getElements().add(new ArcTo(radius, radius, 0,
                centerX + (clockwise ? 0 : -radius), centerY + radius, false, clockwise));

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(1));
        pathTransition.setNode(shape);
        pathTransition.setPath(path);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);
        pathTransition.play();
    }
}
