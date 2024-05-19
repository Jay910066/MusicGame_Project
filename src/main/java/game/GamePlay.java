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
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Screen;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 遊戲畫面
 */
public class GamePlay extends Pane {
    private ScreenManager screenManager;
    private int selectedIndex;
    private File selectedSong;
    private double centerX;
    private double centerY;
    private ImageView background;
    private ImageView PlayField;
    private Timer timer;
    private BeatMap beatMap;

    /**
     * 遊戲畫面
     * @param screenManager 畫面管理器
     * @param selectedIndex 歌曲索引，紀錄之前停留在哪首歌曲
     * @param selectedSong 選擇的歌曲
     */
    public GamePlay(ScreenManager screenManager, int selectedIndex, File selectedSong) {
        this.screenManager = screenManager;
        this.selectedIndex = selectedIndex;
        this.selectedSong = selectedSong;
        centerX = Screen.getPrimary().getVisualBounds().getWidth() / 2;
        centerY = Screen.getPrimary().getVisualBounds().getHeight() / 2;
        this.setFocusTraversable(true);
        this.requestFocus();

        ReadOsu readOsu = new ReadOsu(selectedSong.getPath() + "/Info.osu");

        System.out.println(readOsu.getTitle());
        System.out.println(readOsu.getArtist());
        System.out.println(readOsu.getCreator());

        setBackground();

        PlayField = new ImageView(new Image("file:Resources/Images/PlayField.png"));
        getChildren().add(PlayField);
        System.out.println(PlayField.getBoundsInParent().getWidth());
        System.out.println(centerX);
        System.out.println(PlayField.getBoundsInParent().getHeight());
        System.out.println(centerY);
        PlayField.setX(centerX - (PlayField.getBoundsInParent().getWidth() / 2) - 300);
        PlayField.setY(centerY - (PlayField.getBoundsInParent().getHeight() / 2));

        Line trackD = new Line(centerX - 300, 0, centerX - 300, centerY * 2);
        Line trackF = new Line(centerX - 100, 0, centerX - 100, centerY * 2);
        Line trackJ = new Line(centerX + 100, 0, centerX + 100, centerY * 2);
        Line trackK = new Line(centerX + 300, 0, centerX + 300, centerY * 2);
        getChildren().addAll(trackD, trackF, trackJ, trackK);

        beatMap = readOsu.getBeatMap();

        Media playSong = new Media(new File(selectedSong, "song.mp3").toURI().toString());
        MediaPlayer songPlayer = new MediaPlayer(playSong);
        songPlayer.setOnReady(() -> {
            songPlayer.play();
            timer = new Timer();
        });
        AtomicBoolean exit = new AtomicBoolean(false);
        AtomicBoolean pause = new AtomicBoolean(false);

        while(!exit.get()){

        }

        LeaveWindow leaveWindow = new LeaveWindow();
        leaveWindow.confirmButton.setOnAction(event -> {
            exit.set(true);
            screenManager.switchToSongListMenu(selectedIndex);
        });
        leaveWindow.retryButton.setOnAction(event -> {
            getChildren().remove(leaveWindow);
            screenManager.switchToGamePlay(selectedSong, selectedIndex);
        });
        leaveWindow.continueButton.setOnAction(event -> {
            getChildren().remove(leaveWindow);
            songPlayer.play();
            pause.set(false);
            timer.resume();
        });

        Text Dtime = new Text("D:");
        Dtime.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Text Ftime = new Text("F:");
        Ftime.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Text Jtime = new Text("J:");
        Jtime.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Text Ktime = new Text("K:");
        Ktime.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox timeBox = new HBox();
        getChildren().add(timeBox);
        timeBox.setLayoutX(centerX - 200);
        timeBox.setLayoutY(centerY - 300);
        timeBox.setAlignment(Pos.CENTER);
        timeBox.getChildren().addAll(Dtime, Ftime, Jtime, Ktime);

        this.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.D){
                Dtime.setText("D: " + timer.getCurrentTime());
            }
            if(e.getCode() == KeyCode.F){
                Ftime.setText("F: " + timer.getCurrentTime());
            }
            if(e.getCode() == KeyCode.J){
                Jtime.setText("J: " + timer.getCurrentTime());
            }
            if(e.getCode() == KeyCode.K){
                Ktime.setText("K: " + timer.getCurrentTime());
            }
            if(e.getCode() == KeyCode.ESCAPE){
                if(!getChildren().contains(leaveWindow)) {
                    getChildren().add(leaveWindow);
                    leaveWindow.setLayoutX(centerX - 400);
                    leaveWindow.setLayoutY(centerY - 300);
                    songPlayer.pause();
                    pause.set(true);
                    timer.pause();
                }
            }
        });
    }

    private void setBackground(){
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

        if(new File("file:" + selectedSong.getPath() + "/background.jpg").exists()){
            background.setImage(new Image("file:" + selectedSong.getPath() + "/background.jpg"));
        }else {
            background.setImage(new Image("file:" + selectedSong.getPath() + "/cover.jpg"));
        }
    }

    private static class LeaveWindow extends HBox {
        public Button confirmButton = new Button("Yes");
        public Button retryButton = new Button("Retry");
        public Button continueButton = new Button("Continue");

        public LeaveWindow() {
            this.setAlignment(Pos.CENTER);
            this.setSpacing(40);
            this.setMinWidth(800);
            this.setMinHeight(600);

            Label message = new Label("Are you sure you want to exit?");
            message.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

            confirmButton.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            retryButton.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            continueButton.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            this.getChildren().addAll(message, confirmButton, retryButton, continueButton);
            this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        }
    }
}
