package com.example.trafficlightcontrolsystem;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TrafficLightSystem {
    private graph graph;
    private Map<String, Circle[]> trafficLights;
    private Map<String, int[]> intersectionConnections;
    private Timeline trafficControlTimeline;
    private Random random = new Random();

    public TrafficLightSystem(graph graph, Circle lu1, Circle lu2, Circle lu3, Circle lu4,
                              Circle ru1, Circle ru2, Circle ru3, Circle ru4,
                              Circle ld1, Circle ld2, Circle ld3, Circle ld4,
                              Circle rd1, Circle rd2, Circle rd3, Circle rd4) {
        this.graph = graph;
        this.trafficLights = new HashMap<>();
        this.intersectionConnections = new HashMap<>();

        // Her kavşağın ışıklarını kaydet
        trafficLights.put("lu", new Circle[]{lu1, lu2, lu3, lu4}); // Sol üst
        trafficLights.put("ru", new Circle[]{ru1, ru2, ru3, ru4}); // Sağ üst
        trafficLights.put("ld", new Circle[]{ld1, ld2, ld3, ld4}); // Sol alt
        trafficLights.put("rd", new Circle[]{rd1, rd2, rd3, rd4}); // Sağ alt

        // Her kavşağa gelen yönleri tanımla (ışık indeksi sırasına göre)
        // Index 0: Soldan, 1: Üstten, 2: Alttan, 3: Sağdan gelen
        intersectionConnections.put("lu", new int[]{7, 0, 10, 9});  // Sol üst kavşak (Node 8)
        intersectionConnections.put("ru", new int[]{8, 1, 11, 2});  // Sağ üst kavşak (Node 9)
        intersectionConnections.put("ld", new int[]{6, 8, 5, 11});  // Sol alt kavşak (Node 10)
        intersectionConnections.put("rd", new int[]{10, 9, 4, 3});  // Sağ alt kavşak (Node 11)
    }

    public void startTrafficControl() {
        // Başlangıçta tüm ışıkları kırmızı yap
        setAllLightsRed();
        // 4 saniye bekle, sonra trafik kontrolünü başlat
        Timeline initialDelay = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> {
                    startTrafficCycle();
                })
        );
        initialDelay.play();
    }

    private void startTrafficCycle() {
        trafficControlTimeline = new Timeline(
                new KeyFrame(Duration.millis(500), e -> {
                    controlTrafficLights();
                })
        );
        trafficControlTimeline.setCycleCount(Timeline.INDEFINITE);
        trafficControlTimeline.play();
    }

    private void controlTrafficLights() {
        forceCleanupGraph();

        setAllLightsRed();

        System.out.println("\n=== ARAÇ DURUMU ===");
        for(int i = 0; i < 12; i++) {
            for(int j = 0; j < 12; j++) {
                Edge edge = graph.getEdge(i, j);
                if(edge != null && edge.vehicleCount > 0) {
                    System.out.println("Edge " + i + "->" + j + ": " + edge.vehicleCount + " araç");
                }
            }
        }
        System.out.println("===================\n");

        int[] intersectionNodes = {8, 9, 10, 11};
        String[] intersectionNames = {"lu", "ru", "ld", "rd"};

        for(int i = 0; i < intersectionNodes.length; i++) {
            int nodeId = intersectionNodes[i];
            String intersection = intersectionNames[i];

            int busiestDirection = findBusiestDirectionAtIntersection(intersection);
            if (busiestDirection != -1) {
                setLightGreen(intersection, busiestDirection);
            }
        }
    }

    private void forceCleanupGraph() {
        for(int i = 0; i < 12; i++) {
            for(int j = 0; j < 12; j++) {
                Edge edge = graph.getEdge(i, j);
                if(edge != null && edge.vehicleCount > 3) { // 3'ten fazla araç varsa zorla sıfırla
                    System.out.println("ZORLA TEMİZLEME: Edge " + i + "->" + j + " (" + edge.vehicleCount + " araç)");
                    edge.vehicleCount = 0;
                }
            }
        }
    }

    private int findBusiestDirectionAtIntersection(String intersection) {
        int[] incomingNodes = intersectionConnections.get(intersection);
        int targetNode = getNodeIdFromIntersection(intersection);

        int maxTraffic = -1;
        int busiestDirection = -1;

        // Bu kavşağa gelen her yönün araç sayısını kontrol et
        for (int i = 0; i < incomingNodes.length; i++) {
            int fromNode = incomingNodes[i];
            Edge edge = graph.getEdge(fromNode, targetNode);

            if (edge != null) {
                int vehicleCount = edge.vehicleCount;
                String[] dirNames = {"Sol", "Üst", "Alt", "Sağ"};

                if (vehicleCount > maxTraffic ||
                        (vehicleCount == maxTraffic && vehicleCount > 0 && random.nextBoolean())) {
                    maxTraffic = vehicleCount;
                    busiestDirection = i;
                }
            }
        }

        // En az 1 araç varsa o yönü seç, yoksa -1 döndür
        int result = maxTraffic > 0 ? busiestDirection : -1;

        // BURAYI EKLE:
        if (result == -1) {
            result = random.nextInt(4); // 0-3 arası rastgele
        }

        return result;
    }

    private int getNodeIdFromIntersection(String intersection) {
        switch (intersection) {
            case "lu": return 8;  // Sol üst
            case "ru": return 9;  // Sağ üst
            case "ld": return 10; // Sol alt
            case "rd": return 11; // Sağ alt
            default: return -1;
        }
    }

    private void setAllLightsRed() {
        for (Circle[] lights : trafficLights.values()) {
            for (Circle light : lights) {
                light.setFill(Color.RED);
            }
        }
    }

    private void setLightGreen(String intersection, int direction) {
        Circle[] lights = trafficLights.get(intersection);
        if (lights != null && direction >= 0 && direction < lights.length) {
            lights[direction].setFill(Color.GREEN);
        }
    }

    public boolean isLightGreen(String intersection, int direction) {
        Circle[] lights = trafficLights.get(intersection);
        if (lights != null && direction >= 0 && direction < lights.length) {
            return lights[direction].getFill().equals(Color.GREEN);
        }
        return false;
    }

    public void stopTrafficControl() {
        if (trafficControlTimeline != null) {
            trafficControlTimeline.stop();
        }
    }
}