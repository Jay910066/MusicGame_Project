package game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class SoundEffect {
    private static Media tapSound;
    private static Media hitSound;
    private MediaPlayer tapSoundPlayer;
    private MediaPlayer hitSoundPlayer;

    SoundEffect() {
        tapSound = new Media(new File("Resources/Audio/Tap.mp3").toURI().toString());
        hitSound = new Media(new File("Resources/Audio/Hit.mp3").toURI().toString());
        tapSoundPlayer = new MediaPlayer(tapSound);
        hitSoundPlayer = new MediaPlayer(hitSound);
    }

    public void playSoundEffect(Judge judge) {
        if(judge == game.Judge.NONE) {
            playTapSound();
        }else if(judge != Judge.MISS) {
            playHitSound();
        }
    }

    public void playTapSound() {
        tapSoundPlayer.seek(Duration.ZERO);
        tapSoundPlayer.play();
    }

    public void playHitSound() {
        hitSoundPlayer.seek(Duration.ZERO);
        hitSoundPlayer.play();
    }
}
