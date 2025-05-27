package com.example.trafficlightcontrolsystem;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class TrafficController {
    @FXML private Stage stage;
    @FXML private Scene scene;
    @FXML private Parent root;
    @FXML private AnchorPane mainPane;
    @FXML private Circle lu1, lu2, lu3, lu4;
    @FXML private Circle ru1, ru2, ru3, ru4;
    @FXML private Circle ld1, ld2, ld3, ld4;
    @FXML private Circle rd1, rd2, rd3, rd4;

    private TrafficLightSystem trafficLightSystem;
    private graph g;
    private Timeline carGenerationTimeline;
    private boolean trafficSystemReady = false;

    public void initialize() {
        Random rand = new Random();
        g = new graph(12);

        // Graph'ı oluştur
        setupGraph();

        // Trafik ışığı sistemini başlat
        trafficLightSystem = new TrafficLightSystem(g, lu1, lu2, lu3, lu4,
                ru1, ru2, ru3, ru4,
                ld1, ld2, ld3, ld4,
                rd1, rd2, rd3, rd4);
        // 4 saniye sonra trafik sistemi hazır olacak ve araçlar çıkmaya başlayacak
        Timeline systemReadyTimer = new Timeline(
                new KeyFrame(Duration.seconds(4), e -> {
                    trafficSystemReady = true;
                    startCarGeneration();
                })
        );
        systemReadyTimer.play();

        trafficLightSystem.startTrafficControl();
    }

    private void startCarGeneration() {
        // Araç oluşturma timeline'ı - sadece trafik sistemi hazır olduktan sonra
        carGenerationTimeline = new Timeline(
                new KeyFrame(Duration.millis(2500), e -> {
                    if (trafficSystemReady) {
                        DestinationMaker dm = new DestinationMaker(g, mainPane, trafficLightSystem);
                    }
                })
        );
        carGenerationTimeline.setCycleCount(Timeline.INDEFINITE);
        carGenerationTimeline.play();
    }

    private void setupGraph() {
        // Kenar yollar (dış çevre)
        g.addEdge(7,8,3,2,-30,140,120,140);      // Sol giriş -> Sol üst kavşak
        g.addEdge(8,7,3,2,120,106,-30,106);      // Sol üst kavşak -> Sol çıkış
        g.addEdge(0,8,2,2,155,-30,155,70);       // Üst giriş -> Sol üst kavşak
        g.addEdge(8,0,2,2,187,70,187,-30);       // Sol üst kavşak -> Üst çıkış
        g.addEdge(1,9,2,2,790,-30,790,70);       // Üst giriş -> Sağ üst kavşak
        g.addEdge(9,1,2,2,822,70,822,-30);       // Sağ üst kavşak -> Üst çıkış
        g.addEdge(2,9,3,2,1000,106,843,106);     // Sağ giriş -> Sağ üst kavşak
        g.addEdge(9,2,3,2,843,140,1000,140);     // Sağ üst kavşak -> Sağ çıkış
        g.addEdge(3,11,3,2,1000,533,843,533);    // Sağ giriş -> Sağ alt kavşak
        g.addEdge(11,3,3,2,843,560,1000,560);    // Sağ alt kavşak -> Sağ çıkış
        g.addEdge(4,11,3,2,822,700,822,584);     // Alt giriş -> Sağ alt kavşak
        g.addEdge(11,4,3,2,790,584,790,700);     // Sağ alt kavşak -> Alt çıkış
        g.addEdge(5,10,3,2,187,700,187,584);     // Alt giriş -> Sol alt kavşak
        g.addEdge(10,5,3,2,155,584,155,700);     // Sol alt kavşak -> Alt çıkış
        g.addEdge(6,10,3,2,-30,560,120,560);     // Sol giriş -> Sol alt kavşak
        g.addEdge(10,6,3,2,120,533,-30,533);     // Sol alt kavşak -> Sol çıkış

        // Kavşaklar arası bağlantılar
        g.addEdge(8,9,15,5,210,140,753,140);     // Sol üst -> Sağ üst (yatay)
        g.addEdge(9,8,15,5,753,106,210,106);     // Sağ üst -> Sol üst (yatay)
        g.addEdge(10,11,15,5,210,560,753,560);   // Sol alt -> Sağ alt (yatay)
        g.addEdge(11,10,15,5,753,533,210,533);   // Sağ alt -> Sol alt (yatay)
        g.addEdge(8,10,12,5,155,160,155,494);    // Sol üst -> Sol alt (dikey)
        g.addEdge(10,8,12,5,187,494,187,160);    // Sol alt -> Sol üst (dikey)
        g.addEdge(9,11,12,5,790,160,790,494);    // Sağ üst -> Sağ alt (dikey)
        g.addEdge(11,9,12,5,822,494,822,160);    // Sağ alt -> Sağ üst (dikey)
    }

    private void animateCar(Rectangle car) {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(car);
        transition.setDuration(Duration.seconds(1));
        transition.setFromX(0);
        transition.setToX(50);
        transition.setAutoReverse(false);
        transition.play();
    }

    public void switchToMainPage(ActionEvent e) throws IOException {
        // Trafik kontrolünü durdur
        if (trafficLightSystem != null) {
            trafficLightSystem.stopTrafficControl();
        }
        if (carGenerationTimeline != null) {
            carGenerationTimeline.stop();
        }

        //Ana ekrana dönüş yaparken mevcut araçları temizle
        mainPane.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Rectangle);

        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("anasayfa.fxml")));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}