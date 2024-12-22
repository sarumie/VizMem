package com.imura.VizMem;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GameplayController {
    @FXML
    private VBox mainCanvas;
    @FXML
    private Text currRoundText;
    @FXML
    private Text bestRecordText;

    HBox[] rows = new HBox[3];
    Rectangle[] board = new Rectangle[9];
    int currRound = 1, currStep = 0;
    int[] targetSteps, currSteps;

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
    }

    public void previewRound() throws InterruptedException {
        targetSteps = new int[currRound];
        currRoundText.setText("Round " + currRound);

//        loop based on round number and get element with id box_random with delay of 500ms
        for (int i = 0; i < currRound; i++) {
            int randNum = (int) (Math.random() * 9) + 1;
            targetSteps[i] = randNum;
//            PR: add animation keyframe of color change(may be use RGB color)
            Rectangle r = (Rectangle) mainCanvas.lookup("#board_" + randNum);
            r.setFill(javafx.scene.paint.Color.web("38BDF8"));
            Thread.sleep(500);
            r.setFill(javafx.scene.paint.Color.web("E0F2FE"));
        }
    }

    public boolean checkStep(int step) {
        boolean result = step == targetSteps[currStep];
        ++currStep;

        return result;
    }

    public void setBestRecordText(int bestRecord) {
        bestRecordText.setText(bestRecord + "round");
    }

    public void stopRound() {
        setBestRecordText(currRound);
        currStep = 1;
        currRound = 1;
        currRoundText.setText("Round " + currRound);
    }

    @FXML
    public void onBoardClick(Event e) {
        Rectangle nSource = (Rectangle) e.getSource();

//        getID of the rectangle and get the string after the underscore
        String[] parts = nSource.getId().split("_");
        int boardNumber = Integer.parseInt(parts[1]);
        if (checkStep(boardNumber)) {
            currRound++;
        } else {
            stopRound();
        }

//        Change the color of the rectangle when clicked
        if (nSource.getFill().equals(javafx.scene.paint.Color.web("E0F2FE"))) {
            nSource.setFill(javafx.scene.paint.Color.web("38BDF8"));
        } else {
            nSource.setFill(javafx.scene.paint.Color.web("E0F2FE"));
        }

    }
}