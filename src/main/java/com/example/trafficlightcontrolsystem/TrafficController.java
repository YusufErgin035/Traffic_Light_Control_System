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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrafficController {
    @FXML private Stage stage;
    @FXML private Scene scene;
    @FXML private Parent root;
    @FXML private AnchorPane mainPane;
    @FXML private Circle lu1, lu2, lu3, lu4;
    @FXML private Circle ru1, ru2, ru3, ru4;
    @FXML private Circle ld1, ld2, ld3, ld4;
    @FXML private Circle rd1, rd2, rd3, rd4;

    private List<Car> cars = new ArrayList<>();
    private TrafficLightSystem trafficLightSystem;
    private graph g;
    private Timeline carGenerationTimeline;
    private boolean trafficSystemReady = false;

    public void initialize() {
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
        g.addEdge(7,8,3,2,-18,145,132,145);      // Sol giriş -> Sol üst kavşak
        g.addEdge(8,7,3,2,132,114,-18,114);      // Sol üst kavşak -> Sol çıkış
        g.addEdge(0,8,2,2,164,-18,164,82);       // Üst giriş -> Sol üst kavşak
        g.addEdge(8,0,2,2,195,82,195,-18);       // Sol üst kavşak -> Üst çıkış
        g.addEdge(1,9,2,2,797,-18,797,82);       // Üst giriş -> Sağ üst kavşak
        g.addEdge(9,1,2,2,829,82,829,-18);       // Sağ üst kavşak -> Üst çıkış
        g.addEdge(2,9,3,2,1012,114,855,114);     // Sağ giriş -> Sağ üst kavşak
        g.addEdge(9,2,3,2,855,145,1012,145);     // Sağ üst kavşak -> Sağ çıkış
        g.addEdge(3,11,3,2,1012,536,855,536);    // Sağ giriş -> Sağ alt kavşak
        g.addEdge(11,3,3,2,855,570,1012,570);    // Sağ alt kavşak -> Sağ çıkış
        g.addEdge(4,11,3,2,829,712,829,596);     // Alt giriş -> Sağ alt kavşak
        g.addEdge(11,4,3,2,797,596,797,712);     // Sağ alt kavşak -> Alt çıkış
        g.addEdge(5,10,3,2,195,712,195,596);     // Alt giriş -> Sol alt kavşak
        g.addEdge(10,5,3,2,164,596,164,712);     // Sol alt kavşak -> Alt çıkış
        g.addEdge(6,10,3,2,-18,570,132,570);     // Sol giriş -> Sol alt kavşak
        g.addEdge(10,6,3,2,132,536,-30,536);     // Sol alt kavşak -> Sol çıkış

        // Kavşaklar arası bağlantılar
        g.addEdge(8,9,15,5,222,145,765,145);     // Sol üst -> Sağ üst (yatay)
        g.addEdge(9,8,15,5,765,114,222,114);     // Sağ üst -> Sol üst (yatay)
        g.addEdge(10,11,15,5,222,570,765,570);   // Sol alt -> Sağ alt (yatay)
        g.addEdge(11,10,15,5,765,536,222,536);   // Sağ alt -> Sol alt (yatay)
        g.addEdge(8,10,12,5,164,172,164,506);    // Sol üst -> Sol alt (dikey)
        g.addEdge(10,8,12,5,195,506,195,172);    // Sol alt -> Sol üst (dikey)
        g.addEdge(9,11,12,5,797,172,797,506);    // Sağ üst -> Sağ alt (dikey)
        g.addEdge(11,9,12,5,829,506,829,172);    // Sağ alt -> Sağ üst (dikey)
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
        stage.setWidth(600);
        stage.setHeight(400);
        stage.setScene(scene);
        stage.show();
    }
}