package game;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.io.File;

public class PlayGame extends StackPane {
    private ImageView background;

    public PlayGame(ScreenManager screenManager, int selectedIndex, File selectedSong) {
        background = new ImageView();
        getChildren().add(background);
        ColorAdjust brightness = new ColorAdjust();
        brightness.setBrightness(-0.5);
        background.setEffect(brightness);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double maxDimension = Math.max(screenBounds.getWidth(), screenBounds.getHeight());
        background.setPreserveRatio(true);
        if(background.getFitHeight() < background.getFitWidth())
            background.setFitHeight(maxDimension * 1);
        else
            background.setFitWidth(maxDimension * 1);

        this.setFocusTraversable(true);
        this.requestFocus();

        if(new File("file:" + selectedSong.getPath() + "/background.jpg").exists()){
            background.setImage(new Image("file:" + selectedSong.getPath() + "/background.jpg"));
        }else {
            background.setImage(new Image("file:" + selectedSong.getPath() + "/cover.jpg"));
        }

        Media playSong = new Media(new File(selectedSong, "song.mp3").toURI().toString());
        MediaPlayer songPlayer = new MediaPlayer(playSong);
        songPlayer.play();

        HBox dialogBox = new HBox(20);
        dialogBox.setAlignment(Pos.CENTER);
        Label message = new Label("Are you sure you want to exit?");
        Button confirmButton = new Button("Yes");
        confirmButton.setOnAction(event -> screenManager.switchToSongListMenu(selectedIndex));
        Button retryButton = new Button("Retry");
        retryButton.setOnAction(event -> {
            getChildren().remove(dialogBox);
            screenManager.switchToPlayGame(selectedSong, selectedIndex);
        });
        Button continueButton = new Button("Continue");
        continueButton.setOnAction(event -> {
            getChildren().remove(dialogBox);
            songPlayer.play();
        });

        dialogBox.getChildren().addAll(message, confirmButton, retryButton, continueButton);
        dialogBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");


        this.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE){
                if(!getChildren().contains(dialogBox)) {
                    getChildren().add(dialogBox);
                    songPlayer.pause();
                }
            }
        });
    }
}
