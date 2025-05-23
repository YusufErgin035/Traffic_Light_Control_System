package com.example.trafficlightcontrolsystem;

import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

public class Car {
    Rectangle shape;
    double speed;
    private double currentAngle = 0; // Aracın mevcut açısı (derece)

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

    void moveX() {
        shape.setTranslateX(shape.getTranslateX() + speed);
    }

    void moveY() {
        shape.setTranslateY(shape.getTranslateY() + speed);
    }

    double getCurrentX() {
        return shape.getLayoutX() + shape.getTranslateX();
    }

    /**
     * Edge üzerinde hareket - açıyı edge yönüne göre ayarla
     */
    public void moveAlongEdge(Edge edge) {
        // Edge'in gerçek açısını hesapla
        double deltaX = edge.toX - edge.fromX;
        double deltaY = edge.toY - edge.fromY;
        double edgeAngle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        // Araç konumunu edge başlangıcına ayarla
        shape.setLayoutX(edge.fromX);
        shape.setLayoutY(edge.fromY);

        // Eğer mevcut açı ile edge açısı çok farklıysa, önce döndür
        double angleDifference = normalizeAngle(edgeAngle - currentAngle);

        if (Math.abs(angleDifference) > 5) { // 5 derece tolerans
            // Önce aracı doğru açıya döndür
            RotateTransition rotateFirst = new RotateTransition();
            rotateFirst.setDuration(Duration.seconds(0.2));
            rotateFirst.setNode(shape);
            rotateFirst.setFromAngle(currentAngle);
            rotateFirst.setToAngle(edgeAngle);

            rotateFirst.setOnFinished(e -> {
                currentAngle = edgeAngle;
                // Döndükten sonra hareket et
                startMovement(edge);
            });

            rotateFirst.play();
        } else {
            // Açı farkı azsa direkt hareket et
            shape.setRotate(edgeAngle);
            currentAngle = edgeAngle;
            startMovement(edge);
        }
    }

    /**
     * Edge üzerinde hareket animasyonu
     */
    private void startMovement(Edge edge) {
        Line line = new Line(edge.fromX, edge.fromY, edge.toX, edge.toY);

        PathTransition transition = new PathTransition();
        transition.setDuration(Duration.seconds(edge.roadTime));
        transition.setNode(shape);
        transition.setPath(line);
        transition.setCycleCount(1);
        transition.setAutoReverse(false);

        transition.play();
    }

    /**
     * Kavşak dönüşü - yumuşak geçiş
     */
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

    /**
     * Arc path oluştur
     */
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

    /**
     * Açıyı -180 ile +180 arasına normalize et
     */
    private double normalizeAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}