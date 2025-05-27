package com.example.trafficlightcontrolsystem;

import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Car {
    Rectangle shape;
    double speed;
    private double currentAngle = 0; // Aracın mevcut açısı (derece)
    private boolean isWaiting=false;
    private List<PathTransition> activePathTransitions = new ArrayList<>();
    private List<RotateTransition> activeRotateTransitions = new ArrayList<>();

    // Bekleme durumunu kontrol eden getter ve setter
    public boolean isWaiting() {return isWaiting;}

    public void setWaiting(boolean waiting) {isWaiting = waiting;}

    public Car(double x, double y, Color color, String orientation) {
        // Hep aynı boyutta araç, sadece döndüreceğiz
        shape = new Rectangle(25, 12, color);
        shape.setLayoutX(x);
        shape.setLayoutY(y);
        speed = 20;


        // Başlangıç orientasyonuna göre açıyı ayarla
        if ("y".equalsIgnoreCase(orientation)) {
            currentAngle = 90; // Dikey yol için
            shape.setRotate(currentAngle);
        } else {
            currentAngle = 0; // Yatay yol için
            shape.setRotate(currentAngle);
        }
    }

    public Rectangle getShape() {
        return shape;
    }

    private void startMovement(Edge edge) {
        Line line = new Line(edge.fromX, edge.fromY, edge.toX, edge.toY);

        PathTransition transition = new PathTransition();
        transition.setDuration(Duration.seconds(edge.roadTime));
        transition.setNode(shape);
        transition.setPath(line);
        transition.setCycleCount(1);
        transition.setAutoReverse(false);

        activePathTransitions.add(transition);
        transition.play();
    }

    public void arcTurn(boolean clockwise) {
        // Mevcut konum
        double currentX = shape.getLayoutX() + shape.getTranslateX();
        double currentY = shape.getLayoutY() + shape.getTranslateY();

        // Hedef açı - final yapıyoruz lambda için
        final double targetAngle = normalizeAngle(currentAngle + (clockwise ? 90 : -90));

        // Arc yolu oluştur
        double radius = 25;
        Path arcPath = createArcPath(currentX, currentY, radius, clockwise);

        // Yol animasyonu
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(0.8));
        pathTransition.setNode(shape);
        pathTransition.setPath(arcPath);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);

        // Rotasyon animasyonu
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setDuration(Duration.seconds(0.8));
        rotateTransition.setNode(shape);
        rotateTransition.setFromAngle(currentAngle);
        rotateTransition.setToAngle(targetAngle);

        // Animasyon bittiğinde açıyı güncelle
        pathTransition.setOnFinished(event -> {
            currentAngle = targetAngle;
        });

        // Her iki animasyonu da başlat
        pathTransition.play();
        rotateTransition.play();
    }

    private Path createArcPath(double startX, double startY, double radius, boolean clockwise) {
        // Mevcut yöne göre arc merkezi hesapla
        double radians = Math.toRadians(currentAngle);

        // Perpendicular direction for arc center
        double perpX = -Math.sin(radians) * radius;
        double perpY = Math.cos(radians) * radius;

        if (!clockwise) {
            perpX = -perpX;
            perpY = -perpY;
        }

        double centerX = startX + perpX;
        double centerY = startY + perpY;

        // Bitiş noktası
        double endRadians = Math.toRadians(currentAngle + (clockwise ? 90 : -90));
        double endX = centerX + Math.cos(endRadians) * radius;
        double endY = centerY + Math.sin(endRadians) * radius;

        // Path oluştur
        Path path = new Path();
        path.getElements().add(new MoveTo(startX, startY));

        ArcTo arcTo = new ArcTo();
        arcTo.setX(endX);
        arcTo.setY(endY);
        arcTo.setRadiusX(radius);
        arcTo.setRadiusY(radius);
        arcTo.setSweepFlag(clockwise);
        arcTo.setLargeArcFlag(false);

        path.getElements().add(arcTo);

        return path;
    }

    private double normalizeAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}