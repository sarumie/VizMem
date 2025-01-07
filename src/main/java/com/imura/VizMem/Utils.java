package com.imura.VizMem;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Utils {
    public static void playSound(String soundFile) {
        File f = new File("src/main/resources/audio/" + soundFile);
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static int[] getAddedArrOfInt(int[] arr, int val) {
        int[] newArr = new int[arr.length + 1];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[arr.length] = val;
        return newArr;
    }
}
