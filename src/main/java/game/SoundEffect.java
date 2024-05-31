package game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

/**
 * 音效控制
 */
public class SoundEffect {
    private final MediaPlayer tapSoundPlayer; //點擊音效
    private final MediaPlayer hitSoundPlayer; //擊中音效

    SoundEffect() {
        //載入音效
        Media tapSound = new Media(new File("Resources/Audio/Tap.mp3").toURI().toString());
        tapSoundPlayer = new MediaPlayer(tapSound);

        Media hitSound = new Media(new File("Resources/Audio/Hit.wav").toURI().toString());
        hitSoundPlayer = new MediaPlayer(hitSound);
    }

    /**
     * 播放點擊音效
     */
    public void playTapSound() {
        tapSoundPlayer.seek(Duration.ZERO);
        tapSoundPlayer.setVolume(Settings.effectVolume);
        tapSoundPlayer.play();
    }

    /**
     * 播放擊中音效
     */
    public void playHitSound() {
        hitSoundPlayer.seek(Duration.ZERO);
        hitSoundPlayer.setVolume(Settings.effectVolume);
        hitSoundPlayer.play();
    }
}
