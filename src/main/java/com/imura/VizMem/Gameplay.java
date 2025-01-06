package com.imura.VizMem;

import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.imura.VizMem.Utils.getAddedArrOfInt;

public class Gameplay {
    @Getter
    private int currRound = 1;
    @Getter
    private int currStepIdx = 0;
    @Getter
    @Setter
    private static int tilesXLength;
    @Getter
    @Setter
    private static int tilesYLength;
    @Getter
    @Setter
    private static int totalTiles;
    @Getter
    private int[] targetSteps;
    private Connection conn;
    private int historyID;
    /**
     * 0: not set/invalid
     * 1: easy
     * 2: medium
     * 3: hard
     */
    @Getter
    private static int difficulty = 0;
    private boolean isPlaying = false;

    public Gameplay() {
        conn = new DatabaseManager().getConnection();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public static void setDifficulty(int difficulty) {
        Gameplay.difficulty = difficulty;
        Gameplay.setTilesXLength(2 + difficulty);
        Gameplay.setTilesYLength(2 + difficulty);
        Gameplay.setTotalTiles(Gameplay.getTilesXLength() * Gameplay.getTilesYLength());
    }

    public void startGame() {
        isPlaying = true;
    }

    public void resetGame() {
        currStepIdx = 0;
        currRound = 1;
        isPlaying = false;
    }

    public void prepareNextRound() {
        if (currRound == 1) {
            targetSteps = new int[]{(int) (Math.random() * totalTiles) + 1};
            historyID = (int) (Math.random() * 900000000) + 100000000;
            try {
                conn.createStatement().executeUpdate("INSERT INTO history (id, peak_round, difficulty) VALUES (" + historyID + ", " + currRound + ", " + difficulty + ")");
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
            }
        } else {
            targetSteps = getAddedArrOfInt(targetSteps, (int) (Math.random() * totalTiles) + 1);
            try {
                conn.createStatement().executeUpdate("UPDATE history SET peak_round = " + currRound + " WHERE id = " + historyID);
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
            }
        }
    }

    public boolean isCorrectStep(int step) {
        boolean correct = step == targetSteps[currStepIdx];
        if (correct) {
            currStepIdx++;
        }
        return correct;
    }

    public boolean advanceStep() {
        if (currStepIdx == currRound) {
            currRound++;
            currStepIdx = 0;
            return true;
        }
        return false;
    }

    public String getBestRecord() {
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT COALESCE(MAX(peak_round), 0) FROM history WHERE difficulty = " + difficulty);
            rs.next();
            return rs.getString(1);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}