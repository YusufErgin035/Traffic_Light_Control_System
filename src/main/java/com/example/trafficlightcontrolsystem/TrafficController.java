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
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class TrafficController {
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;
    @FXML
    private AnchorPane mainPane;

    public void initialize() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(1000), e -> {
                    Rectangle car = new Rectangle(30, 15, Color.BLUE);
                    car.setLayoutX(100);
                    car.setLayoutY(100);
                    mainPane.getChildren().add(car);
                    animateCar(car);
                    System.out.println("1 saniye geçti.");
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // Sonsuz döngü
        timeline.play(); // Başlat
    }

    private void animateCar(Rectangle car) {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(car);
        transition.setDuration(Duration.seconds(10));
        transition.setFromX(0);
        transition.setToX(1000);
        transition.setAutoReverse(false);
        transition.play();
    }

    public void switchToMainPage(ActionEvent e) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("anasayfa.fxml")));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
