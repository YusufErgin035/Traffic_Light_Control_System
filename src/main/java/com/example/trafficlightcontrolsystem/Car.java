package com.example.trafficlightcontrolsystem;

import javafx.animation.PathTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

public class Car {
    Rectangle shape;
    double speed;

    public Car(double x, double y, Color color,String orientation) {
        if ("x".equalsIgnoreCase(orientation)) {
            // X ekseninde uzun araç
            shape = new Rectangle(30, 15, color);
        } else if ("y".equalsIgnoreCase(orientation)) {
            // Y ekseninde uzun araç
            shape = new Rectangle(15, 30, color);
        }
        shape.setLayoutX(x);
        shape.setLayoutY(y);
        speed = 20;
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

    public void moveAlongEdge(Edge edge) {
        // Araç başlangıç konumuna ayarlanır
        shape.setLayoutX(edge.fromX);
        shape.setLayoutY(edge.fromY);

        // Çizgisel bir PathTransition ayarlanır
        Line line = new Line(edge.fromX, edge.fromY, edge.toX, edge.toY);

        PathTransition transition = new PathTransition();
        transition.setDuration(Duration.seconds(edge.roadTime)); // Yol süresi kadar animasyon süresi
        transition.setNode(shape);
        transition.setPath(line);
        transition.setCycleCount(1);
        transition.setAutoReverse(false);
        transition.play();
    }
    public void arcTurn(boolean clockwise) {
        // Yarıçap ve merkez konum
        double radius = 10;
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
