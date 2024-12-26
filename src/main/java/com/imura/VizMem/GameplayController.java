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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.imura.VizMem.Utils.getAddedArrOfInt;

public class GameplayController {
    @FXML
    private VBox mainCanvas;
    @FXML
    private Text currRoundText, bestRecordText;
    @FXML
    private Button playOrStopButton;

    private final HBox[] rows = new HBox[3];
    private final Rectangle[] board = new Rectangle[9];
    private int currRound = 1, currStepIdx = 0, isPlaying = 0;
    private int[] targetSteps;
    private Timeline timelinePreview;
    private Connection conn;
    private int historyID;

    public void initialize() {
        conn = new DatabaseManager().getConnection();

        for (int i = 0; i < 3; i++) {
            rows[i] = (HBox) mainCanvas.getChildren().get(i);
            for (int j = 0; j < 3; j++) {
                Rectangle r = (Rectangle) rows[i].getChildren().get(j);
                r.setOnMouseClicked(this::onBoardClick);
                r.setId("board_" + (i * 3 + j + 1));
                board[i * 3 + j] = r;
            }
        }
        disableBoard();
        playOrStopButton.setBackground(Background.fill(Color.web("0284C7")));
        playOrStopButton.setTextFill(Color.web("F0F9FF"));
    }

    public void previewRound() {
        currRoundText.setText("Round " + currRound);
        disableBoard();
        timelinePreview = new Timeline();


        if (currRound == 1) {
            targetSteps = new int[]{(int) (Math.random() * 9) + 1};
            historyID = (int) (Math.random() * 900000000) + 100000000;
            try {
                conn.createStatement().executeUpdate("INSERT INTO history (id, peak_round) VALUES (" + historyID + ", " + currRound + ")");
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
            }
        } else {
            targetSteps = getAddedArrOfInt(targetSteps, (int) (Math.random() * 9) + 1);
            try {
                conn.createStatement().executeUpdate("UPDATE history SET peak_round = " + currRound + " WHERE id = " + currRound);
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
            }
        }

        try {
            conn.createStatement().executeUpdate("UPDATE history SET peak_round = " + currRound + " WHERE id = " + historyID);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        for (int i = 0; i < targetSteps.length; i++) {
            Rectangle r = (Rectangle) mainCanvas.lookup("#board_" + targetSteps[i]);
            timelinePreview.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(i * 1000), e -> r.setFill(Color.web("38BDF8"))),
                    new KeyFrame(Duration.millis(i * 1000 + 500), e -> r.setFill(Color.web("E0F2FE")))
            );
        }

        timelinePreview.setOnFinished(e -> enableBoard());
        timelinePreview.play();
    }

    public boolean isCorrectStep(int step) {
        boolean correct = step == targetSteps[currStepIdx];
        System.out.println(correct ? "Correct step" : "Incorrect step");
        return correct;
    }

    public void stopRound() {
        resetBoard();
        disableBoard();
        playOrStopButton.setText("Play");
        setBestRecordText();
        currStepIdx = 0;
        currRound = 1;
        currRoundText.setText("Round " + currRound);
    }

    public void setBestRecordText() {
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(peak_round) FROM history WHERE id = " + historyID);
            rs.next();
            bestRecordText.setText(rs.getString(1) + " round");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void disableBoard() {
        for (Rectangle r : board) r.setDisable(true);
    }

    public void enableBoard() {
        for (Rectangle r : board) r.setDisable(false);
    }

    public void resetBoard() {
        for (Rectangle r : board) r.setFill(Color.web("E0F2FE"));
    }

    @FXML
    public void onBoardClick(Event e) {
        Rectangle nSource = (Rectangle) e.getSource();
        new Timeline(
                new KeyFrame(Duration.ZERO, eKeyFrame -> nSource.setFill(Color.web("38BDF8"))),
                new KeyFrame(Duration.millis(150), eKeyFrame -> nSource.setFill(Color.web("E0F2FE")))
        ).play();

        int boardNumber = Integer.parseInt(nSource.getId().split("_")[1]);
        if (isCorrectStep(boardNumber)) {
            ++currStepIdx;
            if (currStepIdx == currRound) {
                currRoundText.setText("Round " + ++currRound);
                currStepIdx = 0;
                new Timeline(new KeyFrame(Duration.millis(800), eKeyFrame -> previewRound())).play();
                setBestRecordText();
            }
        } else {
            stopRound();
            playOrStopButton.setText("Play");
            isPlaying = 0;
        }
    }

    @FXML
    public void onPlayOrStopButtonClick() {
        if (isPlaying == 0) {
            playOrStopButton.setText("Stop");
            previewRound();
            isPlaying = 1;
        } else {
            timelinePreview.stop();
            stopRound();
            isPlaying = 0;
        }
    }
}