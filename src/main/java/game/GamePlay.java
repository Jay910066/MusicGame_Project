package game;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 遊戲畫面
 */
public class GamePlay extends Pane {
    private ScreenManager screenManager;
    private File selectedSong;
    private double centerX;
    private double centerY;
    private ImageView background;
    private ImageView PlayField;
    private Timer timer;
    private BeatMap beatMap;
    private Line[] tracks = new Line[4];
    private long startTime;
    private long pauseTime;
    private boolean isPaused = false;
    private AnimationTimer gameLoop;
    private Text gameTimer;
    private List<PathTransition> pathTransitions = new ArrayList<>();
    private List<ScaleTransition> scaleTransitions = new ArrayList<>();
    /**
     * 遊戲畫面
     * @param screenManager 畫面管理器
     * @param selectedSong 選擇的歌曲
     */
    public GamePlay(ScreenManager screenManager, File selectedSong) {
        this.screenManager = screenManager;
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

        tracks[0] = new Line(620, 115, -180, 885);
        tracks[1] = new Line(640, 135, 290, 1615);
        tracks[2] = new Line(680, 135, 1030, 1615);
        tracks[3] = new Line(700, 115, 1500, 885);

        getChildren().addAll(tracks);

        beatMap = readOsu.getBeatMap();

        Media playSong = new Media(new File(selectedSong, "song.mp3").toURI().toString());
        MediaPlayer songPlayer = new MediaPlayer(playSong);
        songPlayer.setOnReady(() -> {
            songPlayer.play();
            timer = new Timer();
            startTime = System.nanoTime();
            /*-----Update Test-----*/

            gameLoop = new AnimationTimer(){

                @Override
                public void handle(long now) {
                    if(isPaused){
                        return;
                    }

                    int gameTime = (int) ((now - startTime) / 1_000_000);
                    update(gameTime);
                }
            };
            gameLoop.start();

            /*-----Update Test-----*/
        });

        gameTimer = new Text("Time: " + 0);
        gameTimer.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        gameTimer.setLayoutX(centerX - 100);
        gameTimer.setLayoutY(centerY - 300);
        getChildren().add(gameTimer);

        AtomicBoolean exit = new AtomicBoolean(false);

        LeaveWindow leaveWindow = new LeaveWindow();
        leaveWindow.confirmButton.setOnAction(event -> {
            exit.set(true);
            screenManager.switchToSongListMenu();
        });
        leaveWindow.retryButton.setOnAction(event -> {
            getChildren().remove(leaveWindow);
            screenManager.switchToGamePlay(selectedSong);
        });
        leaveWindow.continueButton.setOnAction(event -> {
            getChildren().remove(leaveWindow);
            songPlayer.play();
            isPaused = false;
            startTime += System.nanoTime() - pauseTime;
            timer.resume();
            for(PathTransition pathTransition : pathTransitions){
                pathTransition.play();
            }
            for(ScaleTransition scaleTransition : scaleTransitions){
                scaleTransition.play();
            }
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
                    isPaused = true;
                    for(PathTransition pathTransition : pathTransitions){
                        pathTransition.pause();
                    }
                    for(ScaleTransition scaleTransition : scaleTransitions){
                        scaleTransition.pause();
                    }
                    pauseTime = System.nanoTime();
                    timer.pause();
                }
            }
        });
    }

    private void update(int gameTime){
        Note trackDNote = beatMap.getTrack(0).getNotes().get(0);
        Note trackFNote = beatMap.getTrack(1).getNotes().get(0);
        Note trackJNote = beatMap.getTrack(2).getNotes().get(0);
        Note trackKNote = beatMap.getTrack(3).getNotes().get(0);
        if(gameTime > trackDNote.getBornTime()){
            notefall(trackDNote, tracks[0]);
            getChildren().add(trackDNote);
            beatMap.getTrack(0).getNotes().remove(0);
        }
        if(gameTime > trackFNote.getBornTime()){
            notefall(trackFNote, tracks[1]);
            getChildren().add(trackFNote);
            beatMap.getTrack(1).getNotes().remove(0);
        }
        if(gameTime > trackJNote.getBornTime()){
            notefall(trackJNote, tracks[2]);
            getChildren().add(trackJNote);
            beatMap.getTrack(2).getNotes().remove(0);
        }
        if(gameTime > trackKNote.getBornTime()){
            notefall(trackKNote, tracks[3]);
            getChildren().add(trackKNote);
            beatMap.getTrack(3).getNotes().remove(0);
        }
        gameTimer.setText("Time: " + gameTime);
    }

    private void notefall(Note note, Line track){
        PathTransition pathTransition = new PathTransition();
        pathTransition.setNode(note);
        pathTransition.setPath(track);
        pathTransition.setDuration(javafx.util.Duration.seconds(RhythmGame.defaultFlowTime * 2 / Settings.flowSpeed));
        pathTransition.setInterpolator(new NoteFallInterpolation());
        pathTransition.play();

        pathTransitions.add(pathTransition);

        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(note);
        scaleTransition.setDuration(javafx.util.Duration.seconds(RhythmGame.defaultFlowTime * 2 / Settings.flowSpeed));
        scaleTransition.setInterpolator(new NoteFallInterpolation());
        scaleTransition.setFromX(0.12);
        scaleTransition.setFromY(0.12);
        scaleTransition.setToX(2);
        scaleTransition.setToY(2);
        scaleTransition.play();

        scaleTransitions.add(scaleTransition);

        pathTransition.setOnFinished(e -> {
            getChildren().remove(note);

            pathTransitions.remove(pathTransition);
            scaleTransitions.remove(scaleTransition);
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

    public static class NoteFallInterpolation extends Interpolator {
        @Override
        protected double curve(double t) {
            return 2 * t * t;
        }
    }
}
