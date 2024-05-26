package game;

import javafx.animation.*;
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
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;

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
    private ArrayList<Track> bornedNotes;
    private long startTime;
    private long pauseTime;
    private boolean isPaused = false;
    private AnimationTimer gameLoop;
    private Text gameTimer;
    private List<PathTransition> pathTransitions = new ArrayList<>();
    private List<ScaleTransition> scaleTransitions = new ArrayList<>();
    private InputManager inputManager = new InputManager(this);
    ImageView[] judgeEffect = new ImageView[5];

    /**
     * 遊戲畫面
     *
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

        ImageView[] trackPressedEffect = new ImageView[4];
        for(int i = 0; i < 4; i++) {
            trackPressedEffect[i] = new ImageView(new Image("file:Resources/Images/Track" + (i + 1) + "_Pressed.png"));
            trackPressedEffect[i].setX(centerX - (PlayField.getBoundsInParent().getWidth() / 2) - 300);
            trackPressedEffect[i].setY(centerY - (PlayField.getBoundsInParent().getHeight() / 2));
            trackPressedEffect[i].setOpacity(0.5);
            trackPressedEffect[i].setVisible(false);
            getChildren().add(trackPressedEffect[i]);
        }

        judgeEffect[0] = new ImageView(new Image("file:Resources/Images/Perfect+.png"));
        judgeEffect[1] = new ImageView(new Image("file:Resources/Images/Perfect.png"));
        judgeEffect[2] = new ImageView(new Image("file:Resources/Images/Great.png"));
        judgeEffect[3] = new ImageView(new Image("file:Resources/Images/Good.png"));
        judgeEffect[4] = new ImageView(new Image("file:Resources/Images/Bad.png"));
        for(int i = 0; i < 5; i++) {
            judgeEffect[i].setX(centerX);
            judgeEffect[i].setY(centerY);
            judgeEffect[i].setVisible(false);
            getChildren().add(judgeEffect[i]);
        }


        tracks[0] = new Line(620, 110, -180, 940);
        tracks[1] = new Line(640, 135, 260, 1615);//v1,v3:-15
        tracks[2] = new Line(680, 135, 1070, 1615);//v1,v3:-15
        tracks[3] = new Line(700, 110, 1500, 940);

        getChildren().addAll(tracks);

        beatMap = readOsu.getBeatMap();

        bornedNotes = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            bornedNotes.add(new Track());
        }

        Media playSong = new Media(new File(selectedSong, "song.mp3").toURI().toString());
        MediaPlayer songPlayer = new MediaPlayer(playSong);
        songPlayer.setOnReady(() -> {
            songPlayer.setVolume(Settings.volume / 100.0);
            songPlayer.play();
            timer = new Timer();
            startTime = System.nanoTime();
            /*-----Update Test-----*/

            gameLoop = new AnimationTimer() {

                @Override
                public void handle(long now) {
                    if(isPaused) {
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
            for(PathTransition pathTransition : pathTransitions) {
                pathTransition.play();
            }
            for(ScaleTransition scaleTransition : scaleTransitions) {
                scaleTransition.play();
            }
        });

        VBox timeBox = new VBox();
        getChildren().add(timeBox);
        timeBox.setLayoutX(centerX - 200);
        timeBox.setLayoutY(centerY - 300);
        timeBox.setAlignment(Pos.CENTER);

        HBox PressTimeBox = new HBox();
        PressTimeBox.setAlignment(Pos.CENTER);
        String[] key = {"D: ", "F: ", "J: ", "K: "};
        Text[] keyPressTimeTexts = new Text[4];
        for(int i = 0; i < 4; i++) {
            keyPressTimeTexts[i] = new Text(key[0] + 0);
            keyPressTimeTexts[i].setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            keyPressTimeTexts[i].setFill(Color.WHITE);
            PressTimeBox.getChildren().add(keyPressTimeTexts[i]);
        }

        HBox deltaTimeBox = new HBox();
        deltaTimeBox.setAlignment(Pos.CENTER);
        Text[] keyDeltaTimeTexts = new Text[4];
        for(int i = 0; i < 4; i++) {
            keyDeltaTimeTexts[i] = new Text(key[0] + "delta: " + 0);
            keyDeltaTimeTexts[i].setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            keyDeltaTimeTexts[i].setFill(Color.WHITE);
            deltaTimeBox.getChildren().add(keyDeltaTimeTexts[i]);
        }

        HBox ReleaseTimeBox = new HBox();
        ReleaseTimeBox.setAlignment(Pos.CENTER);
        Text[] keyReleaseTimeTexts = new Text[4];
        for(int i = 0; i < 4; i++) {
            keyReleaseTimeTexts[i] = new Text(key[0] + 0);
            keyReleaseTimeTexts[i].setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            keyReleaseTimeTexts[i].setFill(Color.WHITE);
            ReleaseTimeBox.getChildren().add(keyReleaseTimeTexts[i]);
        }

        timeBox.getChildren().addAll(PressTimeBox, deltaTimeBox, ReleaseTimeBox);

        this.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.D) {
                Judge judgement = inputManager.handleKeyPress(KeyCode.D);
                trackPressedEffect[0].setVisible(true);
                keyPressTimeTexts[0].setText("D: " + inputManager.getKeyPressTime(KeyCode.D));
                keyDeltaTimeTexts[0].setText("D_deltaTime: " + inputManager.getDeltaTime(KeyCode.D));
                showJudgement(judgement);
            }
            if(e.getCode() == KeyCode.F) {
                Judge judgement = inputManager.handleKeyPress(KeyCode.F);
                trackPressedEffect[1].setVisible(true);
                keyPressTimeTexts[1].setText("F: " + inputManager.getKeyPressTime(KeyCode.F));
                keyDeltaTimeTexts[1].setText("F_deltaTime: " + inputManager.getDeltaTime(KeyCode.F));
                showJudgement(judgement);
            }
            if(e.getCode() == KeyCode.J) {
                Judge judgement = inputManager.handleKeyPress(KeyCode.J);
                trackPressedEffect[2].setVisible(true);
                keyPressTimeTexts[2].setText("J: " + inputManager.getKeyPressTime(KeyCode.J));
                keyDeltaTimeTexts[2].setText("J_deltaTime: " + inputManager.getDeltaTime(KeyCode.J));
                showJudgement(judgement);
            }
            if(e.getCode() == KeyCode.K) {
                Judge judgement = inputManager.handleKeyPress(KeyCode.K);
                trackPressedEffect[3].setVisible(true);
                keyPressTimeTexts[3].setText("K: " + inputManager.getKeyPressTime(KeyCode.K));
                keyDeltaTimeTexts[3].setText("K_deltaTime: " + inputManager.getDeltaTime(KeyCode.K));
                showJudgement(judgement);
            }
            if(e.getCode() == KeyCode.ESCAPE) {
                if(!getChildren().contains(leaveWindow)) {
                    getChildren().add(leaveWindow);
                    leaveWindow.setLayoutX(centerX - 400);
                    leaveWindow.setLayoutY(centerY - 300);
                    songPlayer.pause();
                    isPaused = true;
                    for(PathTransition pathTransition : pathTransitions) {
                        pathTransition.pause();
                    }
                    for(ScaleTransition scaleTransition : scaleTransitions) {
                        scaleTransition.pause();
                    }
                    pauseTime = System.nanoTime();
                    timer.pause();
                }
            }
        });

        this.setOnKeyReleased(e -> {
            if(e.getCode() == KeyCode.D) {
                inputManager.handleKeyRelease(KeyCode.D);
                trackPressedEffect[0].setVisible(false);
                keyReleaseTimeTexts[0].setText("D: " + inputManager.getKeyReleaseTime(KeyCode.D));
            }
            if(e.getCode() == KeyCode.F) {
                inputManager.handleKeyRelease(KeyCode.F);
                trackPressedEffect[1].setVisible(false);
                keyReleaseTimeTexts[1].setText("F: " + inputManager.getKeyReleaseTime(KeyCode.F));
            }
            if(e.getCode() == KeyCode.J) {
                inputManager.handleKeyRelease(KeyCode.J);
                trackPressedEffect[2].setVisible(false);
                keyReleaseTimeTexts[2].setText("J: " + inputManager.getKeyReleaseTime(KeyCode.J));
            }
            if(e.getCode() == KeyCode.K) {
                inputManager.handleKeyRelease(KeyCode.K);
                trackPressedEffect[3].setVisible(false);
                keyReleaseTimeTexts[3].setText("K: " + inputManager.getKeyReleaseTime(KeyCode.K));
            }
        });
    }

    private void update(int gameTime) {
        spawnNote(0, gameTime);
        spawnNote(1, gameTime);
        spawnNote(2, gameTime);
        spawnNote(3, gameTime);
        gameTimer.setText("Time: " + gameTime);

        missNodeDetection(0, gameTime);
        missNodeDetection(1, gameTime);
        missNodeDetection(2, gameTime);
        missNodeDetection(3, gameTime);
    }

    private void spawnNote(int trackIndex, int gameTime) {
        List<Note> notes = beatMap.getTrack(trackIndex).getNotes();
        if(!notes.isEmpty()) {
            Note note = notes.get(0);
            if(gameTime > note.getBornTime()) {
                noteFall(note, tracks[trackIndex]);
                getChildren().add(note);
                beatMap.getTrack(trackIndex).removeFrontNote();
                bornedNotes.get(trackIndex).addNote(note);
            }
        }
    }

    private void missNodeDetection(int trackIndex, int gameTime) {
        if(!bornedNotes.isEmpty()) {
            List<Note> notes = bornedNotes.get(trackIndex).getNotes();
            if(!notes.isEmpty()) {
                Note frontNote = notes.get(0);
                if(gameTime > frontNote.getHitTime() + RhythmGame.acceptableRange) {
                    if(frontNote instanceof Single singleNote) {
                        getChildren().remove(singleNote);
                        singleNote.miss();
                        bornedNotes.get(trackIndex).removeFrontNote();
                    }else if(frontNote instanceof Hold holdNote) {
                        if(holdNote.isEndNote()) {
                            getChildren().remove(holdNote);
                            holdNote.miss();
                            bornedNotes.get(trackIndex).removeFrontNote();
                        }else if(!holdNote.isEndNote()) {
                            getChildren().remove(holdNote);
                            holdNote.miss();
                            bornedNotes.get(trackIndex).removeFrontNote();
                            if(!notes.isEmpty()) {
                                getChildren().remove(notes.get(0));
                                bornedNotes.get(trackIndex).removeFrontNote();
                            }else {
                                beatMap.getTrack(trackIndex).removeFrontNote();
                            }
                        }
                    }
                }
            }
        }
    }

    private void noteFall(Note note, Line track) {
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
        scaleTransition.setToX(1.88);
        scaleTransition.setToY(1.88);
        scaleTransition.play();

        scaleTransitions.add(scaleTransition);

        pathTransition.setOnFinished(e -> {
            getChildren().remove(note);

            pathTransitions.remove(pathTransition);
            scaleTransitions.remove(scaleTransition);
        });
    }

    private void setBackground() {
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

        if(new File("file:" + selectedSong.getPath() + "/background.jpg").exists()) {
            background.setImage(new Image("file:" + selectedSong.getPath() + "/background.jpg"));
        }else {
            background.setImage(new Image("file:" + selectedSong.getPath() + "/cover.jpg"));
        }
    }

    public ArrayList<Track> getBornedNotes() {
        return bornedNotes;
    }

    public BeatMap getBeatMap() {
        return beatMap;
    }

    public long getStartTime() {
        return startTime;
    }

    public List<PathTransition> getPathTransitions() {
        return pathTransitions;
    }

    public List<ScaleTransition> getScaleTransitions() {
        return scaleTransitions;
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
            return (-t) / (2 * t - 2);
        }//former:2*t*t
    }

    private void showJudgement(Judge judgement){
        int i=0;
        PauseTransition hideJudgement = new PauseTransition(Duration.seconds(0.1));
        switch (judgement) {
            case PERFECT_PLUS:
                judgeEffect[0].setVisible(true);
                hideJudgement.play();
                break;
            case PERFECT:
                judgeEffect[1].setVisible(true);
                i=1;
                hideJudgement.play();
                break;
            case Fast_GREAT,Late_GREAT:
                judgeEffect[2].setVisible(true);
                i=2;
                hideJudgement.play();
                break;
            case Fast_GOOD,Late_GOOD:
                judgeEffect[3].setVisible(true);
                i=3;
                hideJudgement.play();
                break;
            case Fast_BAD,Late_BAD:
                judgeEffect[4].setVisible(true);
                i=4;
                hideJudgement.play();
                break;
            case NONE:
                break;
        }
        int finalI = i;
        hideJudgement.setOnFinished(f-> judgeEffect[finalI].setVisible(false));
    }
}
