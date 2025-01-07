package com.imura.VizMem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("VizMem");
        stage.setScene(scene);
        stage.getIcons().add(new Image("icon.jpg"));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}