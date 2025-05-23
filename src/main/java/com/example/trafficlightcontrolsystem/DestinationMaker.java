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
            return; // Yolun sonuna gelindi, hareket durdurulur
        }

        int from = path.get(index);
        int to = path.get(index + 1);

        // İlgili edge alınır
        Edge edge = g.getEdge(from, to);

        if (edge == null) {
            System.out.println("Kenar bulunamadı: " + from + " -> " + to);
            return;
        }

        // Eğer önceki ve şimdiki koordinatlar arasında bir dönüş olacaksa, işlem yapılır
        if (index > 0) {
            Edge previousEdge = g.getEdge(path.get(index - 1), from); // Önceki kenarı al
            if (previousEdge != null) {
                // Dönüş durumunu kontrol et
                boolean clockwise = shouldTurnClockwise(previousEdge, edge);
                car.arcTurn(clockwise); // Saat yönünde mi, yoksa ters mi döneceğini belirle ve uygula
            }
        }

        // Aracın hareket etmesi
        Line line = new Line(edge.fromX, edge.fromY, edge.toX, edge.toY);
        PathTransition transition = new PathTransition();
        transition.setDuration(Duration.seconds(edge.roadTime)); // Yol süresine bağlı animasyon süresi
        transition.setPath(line);
        transition.setNode(car.getShape());
        transition.setCycleCount(1);
        transition.setAutoReverse(false);

        // Animasyon tamamlandıktan sonra bir sonraki edge üzerinde hareketi başlat
        transition.setOnFinished(event -> moveCarAlongPath(path, index + 1));

        // Animasyonu başlat
        transition.play();
    }
    private boolean shouldTurnClockwise(Edge previousEdge, Edge currentEdge) {
        // Önceki kenarın bitiş noktası ile bir sonraki kenarın başlangıç noktası arasında kontrol
        int deltaX = currentEdge.toX - previousEdge.toX; // X koordinat farkı
        int deltaY = currentEdge.toY - previousEdge.toY; // Y koordinat farkı

        // Sağa dönme için saat yönü dönüşü
        if (deltaX > 0 && deltaY == 0) { // Sağa gidiyorsak
            return true; // Saat yönünde
        }

        // Sola dönme için saat yönünün ters dönüşü
        if (deltaX < 0 && deltaY == 0) { // Sola gidiyorsak
            return false; // Saat yönünün tersi
        }

        // Yukarı veya aşağı gidiyorsak
        if (deltaY != 0) {
            return deltaY > 0; // Eğer yukarı gidiliyorsa, saat yönünde dön
        }

        // Varsayılan durum: saat yönü
        return true;
    }
}
