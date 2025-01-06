package com.imura.VizMem.Controller;

import com.imura.VizMem.Gameplay;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameplayController {
    @FXML
    private VBox mainCanvas;
    @FXML
    private Text currRoundText, bestRecordText;
    @FXML
    private Button playOrStopButton;

    private Rectangle[] tiles;
    private Gameplay gameplay;
    private Timeline timelinePreview;
    private String postFixTitle;

    public void initialize() {
        gameplay = new Gameplay();
        tiles = new Rectangle[Gameplay.getTotalTiles()];

        switch (Gameplay.getDifficulty()) {
            case 1 -> postFixTitle = "Easy";
            case 2 -> postFixTitle = "Medium";
            case 3 -> postFixTitle = "Hard";
            case 0 -> throw new RuntimeException("Invalid difficulty");
        }
        syncCurrRoundText();
        for (int i = 0; i < Gameplay.getTilesXLength(); i++) {
            HBox row = new HBox();
            row.setSpacing(8);
            mainCanvas.getChildren().add(row);
            for (int j = 0; j < Gameplay.getTilesYLength(); j++) {
                Rectangle tile = new Rectangle(78, 78);
                tile.setFill(Color.web("E0F2FE"));
                tile.setSmooth(true);
                tile.setOnMouseClicked(this::onTileClick);
                tile.setId("tile_" + (i * Gameplay.getTilesXLength() + j + 1));
                row.getChildren().add(tile);
                tiles[i * Gameplay.getTilesXLength() + j] = tile;
            }
        }
        disableTiles();
        setBestRecordText();
    }

    public void previewRound() {
        syncCurrRoundText();
        disableTiles();
        timelinePreview = new Timeline();

        gameplay.prepareNextRound();

        for (int i = 0; i < gameplay.getTargetSteps().length; i++) {
            Rectangle tile = (Rectangle) mainCanvas.lookup("#tile_" + gameplay.getTargetSteps()[i]);
            timelinePreview.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(i * 1000), e -> tile.setFill(Color.web("#38BDF8"))),
                    new KeyFrame(Duration.millis(i * 1000 + 500), e -> tile.setFill(Color.web("#E0F2FE")))
            );
        }

        timelinePreview.setOnFinished(e -> enableTiles());
        timelinePreview.play();
    }

    public void stopRound() {
        resetTiles();
        disableTiles();
        playOrStopButton.setText("Play");
        playOrStopButton.setTextFill(Color.web("#172554"));
        playOrStopButton.setStyle("-fx-background-color: #7DD3FC");
        setBestRecordText();
        gameplay.resetGame();
        syncCurrRoundText();
    }

    public void syncCurrRoundText() {
        currRoundText.setText("Round " + gameplay.getCurrRound() + " (" + postFixTitle + ")");
    }

    public void setBestRecordText() {
        bestRecordText.setText(gameplay.getBestRecord() + " round");
    }

    public void disableTiles() {
        for (Rectangle tile : tiles) tile.setDisable(true);
    }

    public void enableTiles() {
        for (Rectangle tile : tiles) tile.setDisable(false);
    }

    public void resetTiles() {
        for (Rectangle tile : tiles) tile.setFill(Color.web("#E0F2FE"));
    }

    @FXML
    public void onTileClick(Event e) {
        Rectangle nSource = (Rectangle) e.getSource();
        int boardNumber = Integer.parseInt(nSource.getId().split("_")[1]);
        timelinePreview.stop();

        if (gameplay.isCorrectStep(boardNumber)) {
            Timeline tileTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO, eKeyFrame -> nSource.setFill(Color.web("#4ADE80"))),
                    new KeyFrame(Duration.millis(200), eKeyFrame -> nSource.setFill(Color.web("#E0F2FE")))
            );
            tileTimeline.play();

            if (gameplay.advanceStep()) {
                syncCurrRoundText();
                setBestRecordText();
                tileTimeline.setOnFinished(eKeyFrame -> new Timeline(new KeyFrame(Duration.millis(300), eKeyFrame2 -> previewRound())).play());
            }
        } else {
            new Timeline(
                    new KeyFrame(Duration.ZERO, eKeyFrame -> nSource.setFill(Color.web("#F87171"))),
                    new KeyFrame(Duration.millis(200), eKeyFrame -> nSource.setFill(Color.web("#E0F2FE")))
            ).play();
            stopRound();
        }
    }

    @FXML
    public void onPlayOrStopButtonClick() {
        if (!gameplay.isPlaying()) {
            gameplay = new Gameplay();
            playOrStopButton.setText("Stop");
            playOrStopButton.setTextFill(Color.web("#450A0A"));
            playOrStopButton.setStyle("-fx-background-color: #FCA5A5");
            previewRound();
            gameplay.startGame();
        } else {
            timelinePreview.stop();
            stopRound();
        }
    }
}