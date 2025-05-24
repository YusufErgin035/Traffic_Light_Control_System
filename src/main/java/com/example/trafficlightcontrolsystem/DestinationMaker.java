package com.example.trafficlightcontrolsystem;

import javafx.animation.PathTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;

public class DestinationMaker {
    private graph g;
    private int start;
    private int end;
    private Car car;
    private AnchorPane pane;
    public DestinationMaker(graph g, AnchorPane pane) {
        Random rand = new Random();
        this.pane = pane;
        this.g = g;
        this.start = rand.nextInt(0,7);
        do{
            this.end = rand.nextInt(0,7);
        }while(this.end == this.start);
        List<Integer> path = g.dijkstra(this.start,this.end);
        double red = rand.nextDouble();
        double green = rand.nextDouble();
        double blue = rand.nextDouble();
        Edge edge = g.getEdge(path.get(0),path.get(1));
        this.car = new Car(0,0,Color.color(red,green,blue), edge.calcLine());
        this.pane.getChildren().add(car.getShape());
        makeDestination(this.start,this.end);
    }
    public void makeDestination(int start, int end) {
        List<Integer> path = g.dijkstra(start, end); // Dijkstra ile en kısa yol bulunur.
        moveCarAlongPath(path, 0); // Yolu takip ederek aracı hareket ettir
    }

    private void moveCarAlongPath(List<Integer> path, int index) {
        if (index >= path.size() - 1) {
            this.pane.getChildren().remove(car.getShape());
            return;
        }

        int from = path.get(index);
        int to = path.get(index + 1);
        Edge edge = g.getEdge(from, to);

        if (edge == null) {
            System.out.println("Kenar bulunamadı: " + from + " -> " + to);
            return;
        }

        // Dönüş kontrolü - sadece gerçek dönüş varsa arcTurn çağır
        if (index > 0) {
            Edge previousEdge = g.getEdge(path.get(index - 1), from);
            if (previousEdge != null && needsTurn(previousEdge, edge)) {
                boolean clockwise = shouldTurnClockwise(previousEdge, edge);
                car.arcTurn(clockwise);
            }
        }

        // Hareket animasyonu
        Line line = new Line(edge.fromX, edge.fromY, edge.toX, edge.toY);
        PathTransition transition = new PathTransition();
        transition.setDuration(Duration.seconds(edge.roadTime));
        transition.setPath(line);
        transition.setNode(car.getShape());
        transition.setCycleCount(1);
        transition.setAutoReverse(false);

        transition.setOnFinished(event -> moveCarAlongPath(path, index + 1));
        transition.play();
    }

    /**
     * Gerçekten dönüş gerekip gerekmediğini kontrol et
     */
    private boolean needsTurn(Edge previousEdge, Edge currentEdge) {
        // Önceki edge açısı
        double prevAngle = Math.toDegrees(Math.atan2(
                previousEdge.toY - previousEdge.fromY,
                previousEdge.toX - previousEdge.fromX
        ));

        // Şimdiki edge açısı
        double currAngle = Math.toDegrees(Math.atan2(
                currentEdge.toY - currentEdge.fromY,
                currentEdge.toX - currentEdge.fromX
        ));

        // Açı farkını hesapla
        double diff = Math.abs(currAngle - prevAngle);
        while (diff > 180) diff -= 360;
        diff = Math.abs(diff);

        // 10 dereceden fazla fark varsa dönüş gerekiyor
        return diff > 10;
    }
    private boolean shouldTurnClockwise(Edge previousEdge, Edge currentEdge) {
        // Önceki edge açısı
        double prevAngle = Math.toDegrees(Math.atan2(
                previousEdge.toY - previousEdge.fromY,
                previousEdge.toX - previousEdge.fromX
        ));

        // Şimdiki edge açısı
        double currAngle = Math.toDegrees(Math.atan2(
                currentEdge.toY - currentEdge.fromY,
                currentEdge.toX - currentEdge.fromX
        ));

        // Açı farkını hesapla
        double diff = currAngle - prevAngle;
        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;

        return diff > 0; // Pozitif = saat yönünde
    }
}
