package game;

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
import javafx.scene.text.Text;
import javafx.stage.Screen;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

public class GamePlay extends StackPane {
    private ImageView background;
    private Instant startTime;

    public GamePlay(ScreenManager screenManager, int selectedIndex, File selectedSong) {
        ReadOsu readOsu = new ReadOsu(selectedSong.getPath() + "/Info.osu");

        System.out.println(readOsu.getTitle());
        System.out.println(readOsu.getArtist());
        System.out.println(readOsu.getCreator());

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
        songPlayer.setOnReady(() -> {
            songPlayer.play();
            startTime = Instant.now();
        });

        HBox leaveWindow = new HBox(20);
        leaveWindow.setAlignment(Pos.CENTER);
        Label message = new Label("Are you sure you want to exit?");
        Button confirmButton = new Button("Yes");
        confirmButton.setOnAction(event -> screenManager.switchToSongListMenu(selectedIndex));
        Button retryButton = new Button("Retry");
        retryButton.setOnAction(event -> {
            getChildren().remove(leaveWindow);
            screenManager.switchToGamePlay(selectedSong, selectedIndex);
        });
        Button continueButton = new Button("Continue");
        continueButton.setOnAction(event -> {
            getChildren().remove(leaveWindow);
            songPlayer.play();
        });

        leaveWindow.getChildren().addAll(message, confirmButton, retryButton, continueButton);
        leaveWindow.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        Text Dtime = new Text("D:");
        Text Ftime = new Text("F:");
        Text Jtime = new Text("J:");
        Text Ktime = new Text("K:");

        HBox timeBox = new HBox();
        getChildren().add(timeBox);
        timeBox.setAlignment(Pos.CENTER);
        timeBox.getChildren().addAll(Dtime, Ftime, Jtime, Ktime);

        this.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.D){
                Dtime.setText("D: " + getElapsedTime());
            }
            if(e.getCode() == KeyCode.F){
                Ftime.setText("F: " + getElapsedTime());
            }
            if(e.getCode() == KeyCode.J){
                Jtime.setText("J: " + getElapsedTime());
            }
            if(e.getCode() == KeyCode.K){
                Ktime.setText("K: " + getElapsedTime());
            }
            if(e.getCode() == KeyCode.ESCAPE){
                if(!getChildren().contains(leaveWindow)) {
                    getChildren().add(leaveWindow);
                    songPlayer.pause();
                }
            }
        });
    }

    private long getElapsedTime() {
        Instant now = Instant.now();
        return java.time.Duration.between(startTime, now).toMillis();
    }
}
