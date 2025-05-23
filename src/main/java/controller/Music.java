package controller;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Music {
    private MediaPlayer moveSound;

    public Music() {
    }

    public void moveSound() {
        loadSound("C:\\Users\\zh\\Downloads\\棋子移动 No.1-将棋-兵_爱给网_aigei_com.mp3");
        playMoveSound();
    }

    public void playWinMusic() {
        // 可以添加类似的逻辑来播放胜利音乐
    }

    public void startMusic() {
        // 可以添加类似的逻辑来播放背景音乐
    }

    public void errorMusic() {
        // 可以添加类似的逻辑来播放错误音效
    }

    private void playMoveSound() {
        if (moveSound != null) {
            moveSound.stop(); // 停止之前的音效
            moveSound.seek(javafx.util.Duration.ZERO); // 重置音效到开头
            moveSound.play(); // 播放音效
        }
    }

    private void loadSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            Media media = new Media(soundFile.toURI().toString());
            moveSound = new MediaPlayer(media);
        } catch (Exception e) {
            System.err.println("音效文件加载失败: " + e.getMessage());
        }
    }
}