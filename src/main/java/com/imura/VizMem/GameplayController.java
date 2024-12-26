package com.imura.VizMem;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
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
    private Text currRoundText;
    @FXML
    private Text bestRecordText;
    @FXML
    private Button playOrStopButton;

    HBox[] rows = new HBox[3];
    Rectangle[] board = new Rectangle[9];
    int currRound = 1, currStepIdx = 0;
    int[] targetSteps, currSteps;
    int isPlaying = 0;

    //    Initialize the board
    public void initialize() {
        for (int i = 0; i < 3; i++) {
            rows[i] = (HBox) mainCanvas.getChildren().get(i);
            for (int j = 0; j < 3; j++) {
                board[i * 3 + j] = (Rectangle) rows[i].getChildren().get(j);
            }
        }

        for (int i = 0; i < board.length; i++) {
            Rectangle r = board[i];
            r.setOnMouseClicked(this::onBoardClick);
            r.setId("board_" + (i + 1));
        }

        disableBoard();

//        re-color button
        playOrStopButton.setBackground(Background.fill(Color.web("0284C7")));
        playOrStopButton.setTextFill(Color.web("F0F9FF"));
    }

    public void previewRound() {
//        TODO: add the targetSteps incrementally and not replace the whole array
        targetSteps = new int[currRound];
        currSteps = new int[currRound];
        currRoundText.setText("Round " + currRound);
        disableBoard();
        Timeline timeline = new Timeline();

        for (int i = 0; i < currRound; i++) {
            int randNum = (int) (Math.random() * 9) + 1;
            targetSteps[i] = randNum;
            Rectangle r = (Rectangle) mainCanvas.lookup("#board_" + randNum);

            timeline.getKeyFrames()
                    .addAll(new KeyFrame(Duration.millis(i * 1000), e -> r.setFill(Color.web("38BDF8"))),
                            new KeyFrame(Duration.millis(i * 1000 + 500), e -> r.setFill(Color.web("E0F2FE"))));
        }

        timeline.setOnFinished(e -> enableBoard());
        timeline.play();
    }

    public boolean isCorrectStep(int step) {
        if (step == targetSteps[currStepIdx]) {
            System.out.println("Correct step");
        } else {
            System.out.println("Incorrect step");
        }

        return step == targetSteps[currStepIdx];
    }

    public void stopRound() {
        resetBoard();
        disableBoard();
        playOrStopButton.setText("Play");
        bestRecordText.setText(currRound + " round");
        currStepIdx = 0;
        currRound = 1;
        currRoundText.setText("Round " + currRound);
    }

    //    dissable board prevent user to click the board
    public void disableBoard() {
        for (Rectangle r : board) {
            r.setDisable(true);
        }
    }

    //    enable board to allow user to click the board
    public void enableBoard() {
        for (Rectangle r : board) {
            r.setDisable(false);
        }
    }

    //    reset the board color
    public void resetBoard() {
        for (Rectangle r : board) {
            r.setFill(Color.web("E0F2FE"));
        }
    }

    @FXML
    public void onBoardClick(Event e) {
        Rectangle nSource = (Rectangle) e.getSource();
        new Timeline(
                new KeyFrame(Duration.ZERO, eKeyFrame -> nSource.setFill(Color.web("38BDF8"))),
                new KeyFrame(Duration.millis(150), eKeyFrame -> nSource.setFill(Color.web("E0F2FE")))
        ).play();

//        getID of the rectangle and get the string after the underscore
        int boardNumber = Integer.parseInt(nSource.getId().split("_")[1]);
        if (isCorrectStep(boardNumber)) {
            ++currStepIdx;

            if (currStepIdx == currRound) {
                ++currRound;
                currRoundText.setText("Round " + currRound);
                currStepIdx = 0;
                new Timeline(
                        new KeyFrame(Duration.millis(800), eKeyFrame -> previewRound())
                ).play();
            }

            currSteps[currStepIdx] = boardNumber;
        } else {
            stopRound();
            playOrStopButton.setText("Play");
            isPlaying = 0;
        }
    }

    @FXML
    public void onPlayOrStopButtonClick() {
        if (isPlaying == 0) {
            System.out.println("Play button clicked");
            playOrStopButton.setText("Stop");
            previewRound();
            isPlaying = 1;
        } else {
            System.out.println("Stop button clicked");
            stopRound();
            isPlaying = 0;
        }
    }
}