package com.example.trafficlightcontrolsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("anasayfa.fxml"));
        Font.loadFont(Objects.requireNonNull(getClass().getResource("/fonts/impact.ttf")).toExternalForm(), 36);
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Mrb!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}