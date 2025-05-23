package com.example.trafficlightcontrolsystem;

import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

public class Car {
    Rectangle shape;
    double speed;

    // Yol paralel kontrol sistemi için eklenen değişkenler
    private double currentRoadDirection = 0; // Mevcut yol yönü (derece)
    private static final double ANGLE_TOLERANCE = 5.0; // Tolerans değeri
    private String currentOrientation; // Aracın mevcut yönelimi

    // Yön sabitleri
    private static final double NORTH = 0;
    private static final double EAST = 90;
    private static final double SOUTH = 180;
    private static final double WEST = 270;

    public Car(double x, double y, Color color, String orientation) {
        this.currentOrientation = orientation;

        if ("x".equalsIgnoreCase(orientation)) {
            // X ekseninde uzun araç (doğu/batı yönünde)
            shape = new Rectangle(30, 15, color);
            currentRoadDirection = EAST; // Başlangıçta doğu yönünde
        } else if ("y".equalsIgnoreCase(orientation)) {
            // Y ekseninde uzun araç (kuzey/güney yönünde)
            shape = new Rectangle(15, 30, color);
            currentRoadDirection = SOUTH; // Başlangıçta güney yönünde
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

        // Yol yönünü edge'e göre güncelle
        updateRoadDirectionFromEdge(edge);

        transition.play();
    }

    /**
     * Edge bilgisinden yol yönünü hesaplar ve günceller
     */
    private void updateRoadDirectionFromEdge(Edge edge) {
        double deltaX = edge.toX - edge.fromX;
        double deltaY = edge.toY - edge.fromY;

        // Yol yönünü hesapla
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            // Yatay hareket
            currentRoadDirection = deltaX > 0 ? EAST : WEST;
        } else {
            // Dikey hareket
            currentRoadDirection = deltaY > 0 ? SOUTH : NORTH;
        }
    }

    /**
     * Geliştirilmiş arcTurn fonksiyonu - yol paralel kontrolü ile
     */
    public void arcTurn(boolean clockwise) {
        double radius = 10;
        double centerX = shape.getLayoutX() + (clockwise ? radius : -radius);
        double centerY = shape.getLayoutY();

        double startAngle = clockwise ? 180 : 0;
        double endAngle = clockwise ? 270 : 90;

        // Hedef yol yönünü hesapla
        double targetRoadDirection = calculateTargetRoadDirection(clockwise);

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

        // Döngü tamamlandığında yol paralel kontrolü yap
        pathTransition.setOnFinished(event -> {
            double finalRotation = clockwise ? 90 : -90;
            shape.setRotate(finalRotation);

            // Yol paralel kontrolü
            checkAndCorrectRoadAlignment(targetRoadDirection);
        });

        pathTransition.play();
    }

    /**
     * Hedef yol yönünü hesaplar
     */
    private double calculateTargetRoadDirection(boolean clockwise) {
        if (clockwise) {
            // Saat yönünde dönüş: 90 derece sağa dön
            return normalizeAngle(currentRoadDirection + 90);
        } else {
            // Saat yönü tersinde dönüş: 90 derece sola dön
            return normalizeAngle(currentRoadDirection - 90);
        }
    }

    /**
     * Aracın yola paralel olup olmadığını kontrol eder ve gerekirse düzeltir
     */
    private void checkAndCorrectRoadAlignment(double targetRoadDirection) {
        double vehicleRotation = normalizeAngle(shape.getRotate());
        double roadDirection = normalizeAngle(targetRoadDirection);

        // Açı farkını hesapla
        double angleDifference = Math.abs(vehicleRotation - roadDirection);

        // 180 dereceden büyük farkları düzelt
        if (angleDifference > 180) {
            angleDifference = 360 - angleDifference;
        }

        boolean isAligned = angleDifference <= ANGLE_TOLERANCE;

        if (isAligned) {
            System.out.println("✓ Araç yola paralel - Açı farkı: " + String.format("%.2f", angleDifference) + "°");
            currentRoadDirection = targetRoadDirection; // Mevcut yol yönünü güncelle
            updateOrientationFromDirection(targetRoadDirection);
            onAlignmentSuccess();
        } else {
            System.out.println("✗ Araç yola paralel değil - Açı farkı: " + String.format("%.2f", angleDifference) + "°");
            correctAlignment(targetRoadDirection);
        }
    }

    /**
     * Hizalama düzeltmesi yapar
     */
    private void correctAlignment(double targetRoadDirection) {
        double vehicleRotation = normalizeAngle(shape.getRotate());
        double roadDirection = normalizeAngle(targetRoadDirection);

        // En kısa yoldan düzeltme açısını hesapla
        double correctionAngle = roadDirection - vehicleRotation;

        if (correctionAngle > 180) {
            correctionAngle -= 360;
        } else if (correctionAngle < -180) {
            correctionAngle += 360;
        }

        // Düzeltme animasyonu
        RotateTransition correction = new RotateTransition(Duration.seconds(0.3), shape);
        correction.setByAngle(correctionAngle);
        correction.setOnFinished(e -> {
            currentRoadDirection = targetRoadDirection;
            updateOrientationFromDirection(targetRoadDirection);
            System.out.println("Hizalama düzeltildi - Yeni yön: " + currentOrientation);
            onAlignmentSuccess();
        });
        correction.play();
    }

    /**
     * Yol yönünden orientation'ı günceller
     */
    private void updateOrientationFromDirection(double direction) {
        double normalizedDir = normalizeAngle(direction);

        if (normalizedDir == EAST || normalizedDir == WEST) {
            currentOrientation = "x";
        } else if (normalizedDir == NORTH || normalizedDir == SOUTH) {
            currentOrientation = "y";
        }
    }

    /**
     * Açıyı 0-360 arasında normalize eder
     */
    private double normalizeAngle(double angle) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * Başarılı hizalama sonrası çağrılır
     */
    private void onAlignmentSuccess() {
        System.out.println("Araç başarıyla yola hizalandı - Yön: " + getCurrentDirectionName());
        // Buraya ek işlemler ekleyebilirsiniz
    }

    /**
     * Mevcut yönün adını döndürür
     */
    private String getCurrentDirectionName() {
        double normalizedDir = normalizeAngle(currentRoadDirection);

        if (normalizedDir == NORTH) return "Kuzey";
        else if (normalizedDir == EAST) return "Doğu";
        else if (normalizedDir == SOUTH) return "Güney";
        else if (normalizedDir == WEST) return "Batı";
        else return "Belirsiz (" + normalizedDir + "°)";
    }

    // Getter ve Setter metodları
    public double getCurrentRoadDirection() {
        return currentRoadDirection;
    }

    public void setCurrentRoadDirection(double direction) {
        this.currentRoadDirection = normalizeAngle(direction);
    }

    public String getCurrentOrientation() {
        return currentOrientation;
    }

    /**
     * Manuel yön ayarlama (debug için)
     */
    public void setManualDirection(double direction) {
        this.currentRoadDirection = normalizeAngle(direction);
        updateOrientationFromDirection(direction);
        System.out.println("Manuel yön ayarlandı: " + getCurrentDirectionName());
    }
}