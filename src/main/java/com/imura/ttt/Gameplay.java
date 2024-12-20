package com.imura.ttt;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class Gameplay {
    @FXML
    private VBox mainCanvas;

    public void initialize() {
        HBox[] rows = new HBox[3];
        Rectangle[] board = new Rectangle[9];
        for (int i = 0; i < 3; i++) {
            rows[i] = (HBox) mainCanvas.getChildren().get(i);
            for (int j = 0; j < 3; j++) {
                board[i * 3 + j] = (Rectangle) rows[i].getChildren().get(j);
            }
        }

//        for index 0, i don't know why it's not inserted
        for (Rectangle r : board) {
            r.setOnMouseClicked(this::onBoardClick);
        }
    }

    @FXML
    public void onBoardClick(Event e) {
        Rectangle nSource = (Rectangle) e.getSource();

        if (nSource.getFill().equals(javafx.scene.paint.Color.RED)) {
            nSource.setFill(javafx.scene.paint.Color.web("0284C7"));
        } else {
            nSource.setFill(javafx.scene.paint.Color.RED);
        }
    }
}