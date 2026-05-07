package com.example.app.services;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.io.File;

/**
 * Video playback service for character lore videos and universe content
 * Supports MP4, WebM, and other JavaFX media formats
 */
public class VideoPlayerService {
    private static MediaPlayer currentPlayer;

    /**
     * Play video file in MediaView
     */
    public static void playVideo(String videoPath, MediaView mediaView) {
        try {
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                throw new IllegalArgumentException("Video file not found: " + videoPath);
            }

            Media media = new Media(videoFile.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);
            mediaView.setMediaPlayer(player);
            player.play();
            currentPlayer = player;
        } catch (Exception e) {
            System.err.println("Error playing video: " + e.getMessage());
        }
    }

    /**
     * Play video by URL (streaming)
     */
    public static void playVideoFromURL(String videoURL, MediaView mediaView) {
        try {
            Media media = new Media(videoURL);
            MediaPlayer player = new MediaPlayer(media);
            mediaView.setMediaPlayer(player);
            player.play();
            currentPlayer = player;
        } catch (Exception e) {
            System.err.println("Error playing video from URL: " + e.getMessage());
        }
    }

    /**
     * Pause current video
     */
    public static void pauseVideo() {
        if (currentPlayer != null) {
            currentPlayer.pause();
        }
    }

    /**
     * Stop and dispose video
     */
    public static void stopVideo() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
        }
    }

    /**
     * Set volume (0.0 to 1.0)
     */
    public static void setVolume(double volume) {
        if (currentPlayer != null) {
            currentPlayer.setVolume(Math.max(0, Math.min(1, volume)));
        }
    }
}
