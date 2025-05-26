package com.example.trafficlightcontrolsystem;

import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.scene.layout.AnchorPane;
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
    private List<Integer> fullPath;
    private TrafficLightSystem trafficSystem;
    private int currentEdgeIndex = 0;

    public DestinationMaker(graph g, AnchorPane pane, TrafficLightSystem trafficSystem) {
        Random rand = new Random();
        this.pane = pane;
        this.g = g;
        this.trafficSystem = trafficSystem;
        this.start = rand.nextInt(0,7);
        do{
            this.end = rand.nextInt(0,7);
        }while(this.end == this.start);

        this.fullPath = g.dijkstra(this.start,this.end);

        // İlk edge'in vehicle count'ını artır (araç yola çıkıyor)
        if (this.fullPath.size() > 1) {
            g.incrementVehicle(this.fullPath.get(0), this.fullPath.get(1));
        }

        double red = rand.nextDouble();
        double green = rand.nextDouble();
        double blue = rand.nextDouble();
        Edge edge = g.getEdge(fullPath.get(0),fullPath.get(1));
        this.car = new Car(0, 0, Color.color(red,green,blue), edge.calcLine());
        this.pane.getChildren().add(car.getShape());
        moveCarAlongPath(0);
    }

    private void moveCarAlongPath( int index) {
        int from = this.fullPath.get(index);
        int to = this.fullPath.get(index + 1);
        Edge edge = g.getEdge(from, to);
        if (edge == null) {
            System.out.println("Kenar bulunamadı: " + from + " -> " + to);
            return;
        }
        moveCarAlongEdge(edge,index);
    }

    private void checkTrafficLightAndMove(List<Integer> path, int index, Edge edge) {
        // Hangi kavşak ve hangi yön olduğunu bul
        int from = this.fullPath.get(index);
        int to = this.fullPath.get(index + 1);
        String intersection = getIntersectionName(to);
        int direction = getDirectionIndex(from,to);

        System.out.println("Araç " + from + "->" + to + " yolunda, kavşak: " + intersection + ", yön: " + direction);

        if (intersection != null && direction != -1) {
            // Işık kontrolü yap
            if (isLightGreen(intersection, direction)) {
                System.out.println("Yeşil ışık - araç geçiyor");
                // Yeşil ışık - geç
                if (index > 0) {
                    this.g.decrementVehicle(path.get(index - 1), path.get(index));
                }
                // Sonraki edge'e gir (eğer varsa)
                if (index + 2 < path.size()) {
                    this.g.incrementVehicle(path.get(index + 1), path.get(index + 2));
                }
                if (index <path.size()-1) {
                    Edge nextEdge = this.g.getEdge(path.get(index+1), path.get(index+2));
                    if (needsTurn(edge, nextEdge)) {
                        boolean clockwise = shouldTurnClockwise(edge, nextEdge);
                        this.car.arcTurn(clockwise);
                    }
                }
                moveCarAlongPath(index+1);
            } else {
                System.out.println("Kırmızı ışık - araç bekliyor");
                // Kırmızı ışık - bekle ve tekrar kontrol et
                if (this.car.isWaiting()) {
                    // Araç zaten bekliyorsa yeni bir Timeline başlatma
                    return;
                }

                this.car.setWaiting(true); // Araç bekleme durumuna geçiyor

                Timeline waitTimeline = new Timeline(
                        new KeyFrame(Duration.millis(500), e -> {
                            // Işık tekrar kontrol ediliyor
                            this.car.setWaiting(false); // Araç önceki döngüsü tamamlandıktan sonra beklemeyi bırakır
                            checkTrafficLightAndMove(path,index,edge);
                        })
                );
                waitTimeline.play();
            }
        } else {
            System.out.println("Intersection bulunamadı - normal hareket");
            // Intersection bulunamadı, normal hareket et
            moveCarAlongEdge(edge,index+1);
        }
    }

    private void moveCarAlongEdge(Edge edge,int index) {
        Line line = new Line(edge.fromX, edge.fromY, edge.toX, edge.toY);
        PathTransition transition = new PathTransition();
        transition.setDuration(Duration.seconds(edge.roadTime));
        transition.setPath(line);
        transition.setNode(this.car.getShape());
        transition.setCycleCount(1);
        transition.setAutoReverse(false);
        if(index==fullPath.size()-2) {
            transition.setOnFinished(event
                    -> {
                System.out.println("Araç hedefe ulaştı, kaldırılıyor...");
                this.g.decrementVehicle(this.fullPath.get(fullPath.size()-2),this.fullPath.get(fullPath.size()-1));
                this.pane.getChildren().remove(car.getShape());
            } );
        }
        else{
            transition.setOnFinished(event
                    -> checkTrafficLightAndMove(fullPath,index,edge));
        }
        transition.play();
    }

    private void performMovement(List<Integer> path, int index, Edge edge) {
        // Dönüş kontrolü
        if (index > 0 && index <path.size()-1) {
            Edge previousEdge = g.getEdge(path.get(index - 1), path.get(index));
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

        transition.setOnFinished(event -> {
            syncGraphWithAnimation();

            if (currentEdgeIndex >= fullPath.size() - 1) {
                // Araç hedefe ulaştı
                this.pane.getChildren().remove(car.getShape());
                return;
            }

            moveCarAlongPath(currentEdgeIndex + 1);
        });
        transition.play();
    }

    private boolean isIntersection(int nodeId) {
        return nodeId == 8 || nodeId == 9 || nodeId == 10 || nodeId == 11;
    }

    private String getIntersectionName(int nodeId) {
        switch (nodeId) {
            case 8: return "lu";  // Sol üst
            case 9: return "ru";  // Sağ üst
            case 10: return "ld"; // Sol alt
            case 11: return "rd"; // Sağ alt
            default: return null;
        }
    }

    private int getDirectionIndex(int from, int to) {
        // Her kavşak için gelen yönlerin indeksini döndür
        switch (to) {
            case 8: // Sol üst kavşak
                if (from == 7) return 0;  // Soldan
                if (from == 0) return 1;  // Üstten
                if (from == 10) return 2; // Alttan
                if (from == 9) return 3;  // Sağdan
                break;
            case 9: // Sağ üst kavşak
                if (from == 8) return 0;  // Soldan
                if (from == 1) return 1;  // Üstten
                if (from == 11) return 2; // Alttan
                if (from == 2) return 3;  // Sağdan
                break;
            case 10: // Sol alt kavşak
                if (from == 6) return 0;  // Soldan
                if (from == 8) return 1;  // Üstten
                if (from == 5) return 2;  // Alttan
                if (from == 11) return 3; // Sağdan
                break;
            case 11: // Sağ alt kavşak
                if (from == 10) return 0; // Soldan
                if (from == 9) return 1;  // Üstten
                if (from == 4) return 2;  // Alttan
                if (from == 3) return 3;  // Sağdan
                break;
        }
        return -1;
    }

    private boolean isLightGreen(String intersection, int direction) {
        // TrafficLightSystem'den ışık durumunu kontrol et
        // Bu metod için TrafficLightSystem'e erişim gerekiyor
        return trafficSystem.isLightGreen(intersection, direction);
    }

    private boolean needsTurn(Edge previousEdge, Edge currentEdge) {
        double prevAngle = Math.toDegrees(Math.atan2(
                previousEdge.toY - previousEdge.fromY,
                previousEdge.toX - previousEdge.fromX
        ));

        double currAngle = Math.toDegrees(Math.atan2(
                currentEdge.toY - currentEdge.fromY,
                currentEdge.toX - currentEdge.fromX
        ));

        double diff = Math.abs(currAngle - prevAngle);
        while (diff > 180) diff -= 360;
        diff = Math.abs(diff);

        return diff > 10;
    }

    private boolean shouldTurnClockwise(Edge previousEdge, Edge currentEdge) {
        double prevAngle = Math.toDegrees(Math.atan2(
                previousEdge.toY - previousEdge.fromY,
                previousEdge.toX - previousEdge.fromX
        ));

        double currAngle = Math.toDegrees(Math.atan2(
                currentEdge.toY - currentEdge.fromY,
                currentEdge.toX - currentEdge.fromX
        ));

        double diff = currAngle - prevAngle;
        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;

        return diff > 0;
    }

    private void syncGraphWithAnimation() {
        // Mevcut konumu graph'tan temizle
        if (currentEdgeIndex > 0 && currentEdgeIndex < fullPath.size()) {
            g.decrementVehicle(fullPath.get(currentEdgeIndex - 1), fullPath.get(currentEdgeIndex));
        }

        // Yeni konuma kaydet
        if (currentEdgeIndex + 1 < fullPath.size()) {
            g.incrementVehicle(fullPath.get(currentEdgeIndex), fullPath.get(currentEdgeIndex + 1));
        }

        currentEdgeIndex++;
    }


}