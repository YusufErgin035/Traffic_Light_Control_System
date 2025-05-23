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
        graph g = new graph(12);
        g.addEdge(7,8,3,2,-30,140,120,140);
        g.addEdge(8,7,3,2,120,106,-30,106);
        g.addEdge(0,8,2,2,155,-30,155,70);
        g.addEdge(8,0,2,2,187,70,187,-30);
        g.addEdge(1,9,2,2,790,-30,790,70);
        g.addEdge(9,1,2,2,822,70,822,-30);
        g.addEdge(2,9,3,2,1000,106,843,106);
        g.addEdge(9,2,3,2,843,140,1000,140);
        g.addEdge(3,11,3,2,1000,533,843,533);
        g.addEdge(11,3,3,2,843,560,1000,560);
        g.addEdge(4,11,3,2,822,700,822,584);
        g.addEdge(11,4,3,2,790,584,790,700);
        g.addEdge(5,10,3,2,187,700,187,584);
        g.addEdge(10,5,3,2,155,584,155,700);
        g.addEdge(6,10,3,2,-30,560,120,560);
        g.addEdge(10,6,3,2,120,533,-30,533);
        //kavşaklar
        g.addEdge(8,9,15,5,210,140,753,140);
        g.addEdge(9,8,15,5,753,106,210,106);
        g.addEdge(10,11,15,5,210,560,753,560);
        g.addEdge(11,10,15,5,753,533,210,533);
        g.addEdge(8,10,12,5,155,160,155,494);
        g.addEdge(10,8,12,5,187,494,187,160);
        g.addEdge(9,11,12,5,790,160,790,494);
        g.addEdge(11,9,12,5,822,494,822,160);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(2500), e -> {
                    double red = rand.nextDouble();
                    double green = rand.nextDouble();
                    double blue = rand.nextDouble();
                    //Car car = new Car(822,494, Color.color(red, green, blue),"y");
                    DestinationMaker dm = new DestinationMaker(g,mainPane);
                    //mainPane.getChildren().add(car.getShape());
//                    Timeline carMovement = new Timeline(
//                            new KeyFrame(Duration.millis(50), ev -> car.moveX())
//                    );
//                    carMovement.setCycleCount(Timeline.INDEFINITE);
//                    carMovement.play();
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
