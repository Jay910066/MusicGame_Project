package game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

/**
 * 音效控制
 */
public class SoundEffect {
    private final MediaPlayer selectSoundPlayer;
    private final MediaPlayer tapSoundPlayer; //點擊音效
    private final MediaPlayer hitSoundPlayer; //擊中音效
    private final MediaPlayer confirmSoundPlayer;
    private final MediaPlayer selectsongSoundPlayer;

    SoundEffect() {
        //載入音效
        Media tapSound = new Media(new File("Resources/Audio/Tap.mp3").toURI().toString());
        tapSoundPlayer = new MediaPlayer(tapSound);

        Media hitSound = new Media(new File("Resources/Audio/Hit.wav").toURI().toString());
        hitSoundPlayer = new MediaPlayer(hitSound);

        Media selectSound = new Media(new File("Resources/Audio/SelectButton.mp3").toURI().toString());
        selectSoundPlayer = new MediaPlayer(selectSound);

        Media confirmSound = new Media(new File("Resources/Audio/confirm.wav").toURI().toString());
        confirmSoundPlayer = new MediaPlayer(confirmSound);

        Media selectsongSound = new Media(new File("Resources/Audio/SelectSong.wav").toURI().toString());
        selectsongSoundPlayer = new MediaPlayer(selectsongSound);
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

    public void playSelectSound() {
        selectSoundPlayer.seek(Duration.ZERO);
        selectSoundPlayer.setVolume(Settings.effectVolume);
        selectSoundPlayer.play();
    }

    public void playComfirmSound() {
        confirmSoundPlayer.seek(Duration.ZERO);
        confirmSoundPlayer.setVolume(Settings.effectVolume);
        confirmSoundPlayer.play();
    }

    public void playSelectSongSound() {
        selectsongSoundPlayer.seek(Duration.ZERO);
        selectsongSoundPlayer.setVolume(Settings.effectVolume);
        selectsongSoundPlayer.play();
    }
}
