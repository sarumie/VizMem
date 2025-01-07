package com.imura.VizMem.Controller;

import com.imura.VizMem.Gameplay;
import com.imura.VizMem.Main;
import com.imura.VizMem.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    @FXML
    private Button easyBtn, mediumBtn, hardBtn;

    public void initialize() {
        easyBtn.setOnMouseClicked(e -> handleButtonClick(1));
        mediumBtn.setOnMouseClicked(e -> handleButtonClick(2));
        hardBtn.setOnMouseClicked(e -> handleButtonClick(3));
    }

    private void handleButtonClick(int difficulty) {
        Utils.playSound("select_diff.wav");

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("gameplay.fxml"));
        Stage currStage = (Stage) easyBtn.getScene().getWindow();
        try {
            Gameplay.setDifficulty(difficulty);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(currStage.getModality());
            stage.initOwner(currStage);
            stage.setTitle("VizMem - " + switch (difficulty) {
                case 1 -> "Easy";
                case 2 -> "Medium";
                case 3 -> "Hard";
                default -> throw new RuntimeException("Invalid difficulty");
            });
            stage.setScene(new Scene(root1));
            currStage.close();
            stage.getIcons().add(new Image("icon.jpg"));
            stage.show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
