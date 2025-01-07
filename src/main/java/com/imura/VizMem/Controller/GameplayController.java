package com.imura.VizMem.Controller;

import com.imura.VizMem.Gameplay;
import com.imura.VizMem.Main;
import com.imura.VizMem.Utils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class GameplayController {
    @FXML
    private VBox mainCanvas;
    @FXML
    private Text currRoundText, bestRecordText;
    @FXML
    private Button playOrStopButton, backBtn;

    private Rectangle[] tiles;
    private Gameplay gameplay;
    private Timeline timelinePreview;
    private String diffTitle;

    public void initialize() {
        gameplay = new Gameplay();
        tiles = new Rectangle[Gameplay.getTotalTiles()];
        diffTitle = switch (Gameplay.getDifficulty()) {
            case 1 -> "Easy";
            case 2 -> "Medium";
            case 3 -> "Hard";
            default -> throw new RuntimeException("Invalid difficulty level");
        };
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
                    new KeyFrame(Duration.millis(i * 1000), e -> {
                        tile.setFill(Color.web("#38BDF8"));
                        Utils.playSound("preview.wav");
                    }),
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
        currRoundText.setText("Round " + gameplay.getCurrRound() + " (" + diffTitle + ")");
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
    public void onTileClick(MouseEvent event) {
        if (!(event.getButton().equals(MouseButton.PRIMARY)) || event.getClickCount() > 1) {
            return;
        }

        timelinePreview.stop();
        Rectangle nSource = (Rectangle) event.getSource();
        int boardNumber = Integer.parseInt(nSource.getId().split("_")[1]);

        if (gameplay.isCorrectStep(boardNumber)) {
            Timeline tileTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO, eKeyFrame -> nSource.setFill(Color.web("#4ADE80"))),
                    new KeyFrame(Duration.millis(200), eKeyFrame -> nSource.setFill(Color.web("#E0F2FE")))
            );
            tileTimeline.play();

            if (gameplay.isNextStep()) {
                Utils.playSound("next_round.wav");
                syncCurrRoundText();
                setBestRecordText();
                tileTimeline.setOnFinished(eKeyFrame -> new Timeline(new KeyFrame(Duration.millis(300), eKeyFrame2 -> previewRound())).play());
                return;
            }
//            Utils.playSound(Math.random() < 0.5 ? "preview.wav" : "correct.wav");
            Utils.playSound("correct.wav");
            return;
        }

//            if step incorrect:
        Utils.playSound("incorrect.wav");
        new Timeline(
                new KeyFrame(Duration.ZERO, eKeyFrame -> nSource.setFill(Color.web("#F87171"))),
                new KeyFrame(Duration.millis(200), eKeyFrame -> nSource.setFill(Color.web("#E0F2FE")))
        ).play();
        stopRound();
    }

    @FXML
    public void onPlayOrStopButtonClick() {
        if (gameplay.isPlaying()) {
            Utils.playSound("stop.wav");
            timelinePreview.stop();
            stopRound();
            return;
        }

        Utils.playSound("start.wav");
        gameplay = new Gameplay();
        playOrStopButton.setText("Stop");
        playOrStopButton.setTextFill(Color.web("#450A0A"));
        playOrStopButton.setStyle("-fx-background-color: #FCA5A5");
        previewRound();
        gameplay.startGame();
    }

    @FXML
    public void onBackBtnClick() throws IOException {
        Utils.playSound("back.wav");
        stopRound();
        Stage currStage = (Stage) backBtn.getScene().getWindow();
        currStage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menu.fxml"));
        Stage stage = new Stage();
        Parent root1 = fxmlLoader.load();
        stage.initModality(currStage.getModality());
        stage.initOwner(currStage);
        stage.setTitle("VizMem");
        stage.setScene(new Scene(root1));
        stage.getIcons().add(new Image("icon.jpg"));
        stage.show();
    }
}