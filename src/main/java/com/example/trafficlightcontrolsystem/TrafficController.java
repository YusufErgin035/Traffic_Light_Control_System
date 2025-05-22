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

    public void initialize() {
        Random rand = new Random();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(5000), e -> {
                    double red = rand.nextDouble();
                    double green = rand.nextDouble();
                    double blue = rand.nextDouble();
                    Car car = new Car(30, 15, Color.color(red, green, blue));
                    mainPane.getChildren().add(car.getShape());
                    Timeline carMovement = new Timeline(
                            new KeyFrame(Duration.millis(2500), ev -> car.arcTurn(true))
                    );
                    carMovement.setCycleCount(Timeline.INDEFINITE);
                    carMovement.play();
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // Sonsuz döngü
        timeline.play(); // Başlat
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
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("anasayfa.fxml")));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
