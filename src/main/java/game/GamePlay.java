package game;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
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
    private BeatMap beatMap;
    private Line[] tracks = new Line[4];
    private ArrayList<Track> bornNotes;
    private boolean[] isHolding = new boolean[4];
    private long startTime;
    private long pauseTime;
    private boolean isPaused = false;
    private AnimationTimer gameLoop;
    private static Text comboText;
    private List<PathTransition> pathTransitions = new ArrayList<>();
    private List<ScaleTransition> scaleTransitions = new ArrayList<>();
    private InputManager inputManager = new InputManager(this);
    ImageView[] judgeEffect = new ImageView[5];

    private static int MAX_WIDTH = 1920;
    private static int MAX_HEIGHT = 1050;

    /**
     * 遊戲畫面
     *
     * @param screenManager 畫面管理器
     * @param selectedSong 選擇的歌曲
     */
    public GamePlay(ScreenManager screenManager, File selectedSong) {
        this.screenManager = screenManager;
        this.selectedSong = selectedSong;
        centerX = MAX_WIDTH / 2;
        centerY = MAX_HEIGHT / 2;
        this.setFocusTraversable(true);
        this.requestFocus();

        ReadOsu readOsu = new ReadOsu(selectedSong.getPath() + "/Info.osu");

        setBackground();

        /*
         * 遊戲畫面
         * */
        PlayField = new ImageView(new Image("file:Resources/Images/PlayField.png"));
        getChildren().add(PlayField);
        PlayField.setX(centerX - (PlayField.getBoundsInParent().getWidth() / 2) - 300);
        PlayField.setY(centerY - (PlayField.getBoundsInParent().getHeight() / 2));

        /*
         * 音軌特效
         * */
        ImageView[] trackPressedEffect = new ImageView[4];
        for(int i = 0; i < 4; i++) {
            trackPressedEffect[i] = new ImageView(new Image("file:Resources/Images/Track" + (i + 1) + "_Pressed.png"));
            trackPressedEffect[i].setX(centerX - (PlayField.getBoundsInParent().getWidth() / 2) - 300);
            trackPressedEffect[i].setY(centerY - (PlayField.getBoundsInParent().getHeight() / 2));
            trackPressedEffect[i].setOpacity(0.5);
            trackPressedEffect[i].setVisible(false);
            getChildren().add(trackPressedEffect[i]);
        }

        comboText = new Text();
        comboText.setStyle("-fx-font-size: 56px; -fx-font-weight: bold;");
        comboText.setFill(Color.WHITE);
        comboText.setX(centerX - (PlayField.getBoundsInParent().getWidth() / 2));
        comboText.setY(centerY - 250);
        getChildren().add(comboText);

        judgeEffect[0] = new ImageView(new Image("file:Resources/Images/Perfect+.png"));
        judgeEffect[1] = new ImageView(new Image("file:Resources/Images/Perfect.png"));
        judgeEffect[2] = new ImageView(new Image("file:Resources/Images/Great.png"));
        judgeEffect[3] = new ImageView(new Image("file:Resources/Images/Good.png"));
        judgeEffect[4] = new ImageView(new Image("file:Resources/Images/Bad.png"));
        for(int i = 0; i < 5; i++) {
            judgeEffect[i].setX(centerX - (PlayField.getBoundsInParent().getWidth() / 2) + i * 22 + 30);
            judgeEffect[i].setY(centerY);
            judgeEffect[i].setVisible(false);
            judgeEffect[i].setEffect(new Glow(0.3));
            getChildren().add(judgeEffect[i]);
        }

        tracks[0] = new Line(620, 110, -180, 940);
        tracks[1] = new Line(642.684, 135, 257.316, 1615);
        tracks[2] = new Line(677.316, 135, 1062.684, 1615);
        tracks[3] = new Line(700, 110, 1500, 940);

        //getChildren().addAll(tracks);

        beatMap = readOsu.getBeatMap();

        bornNotes = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            bornNotes.add(new Track());
        }

        for(int i = 0; i < 4; i++) {
            isHolding[i] = false;
        }

        Media playSong = new Media(new File(selectedSong, "song.mp3").toURI().toString());
        MediaPlayer songPlayer = new MediaPlayer(playSong);
        songPlayer.setOnReady(() -> {
            Judgement.reset();
            songPlayer.setVolume(Settings.volume / 100.0);
            songPlayer.play();
            startTime = System.nanoTime();
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
        });

        songPlayer.setOnEndOfMedia(() -> {
            gameLoop.stop();
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> {
                screenManager.switchToResultsScreen(background, readOsu);
            });
            pause.play();
        });

        /*遊戲時間*/
        //gameTimer = new Text("Time: " + 0);
        //gameTimer.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        //gameTimer.setLayoutX(centerX - 100);
        //gameTimer.setLayoutY(centerY - 300);
        //getChildren().add(gameTimer);


        /*
         *離開視窗
         */
        LeaveWindow leaveWindow = new LeaveWindow();
        leaveWindow.confirmButton.setOnAction(event -> {
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
            for(PathTransition pathTransition : pathTransitions) {
                pathTransition.play();
            }
            for(ScaleTransition scaleTransition : scaleTransitions) {
                scaleTransition.play();
            }
        });

        /*------按下按鍵後的時間資訊------*/
        //VBox timeBox = new VBox();
        //getChildren().add(timeBox);
        //timeBox.setLayoutX(centerX - 200);
        //timeBox.setLayoutY(centerY - 300);
        //timeBox.setAlignment(Pos.CENTER);
        //
        //HBox PressTimeBox = new HBox();
        //PressTimeBox.setAlignment(Pos.CENTER);
        //String[] key = {"D: ", "F: ", "J: ", "K: "};
        //Text[] keyPressTimeTexts = new Text[4];
        //for(int i = 0; i < 4; i++) {
        //    keyPressTimeTexts[i] = new Text(key[0] + 0);
        //    keyPressTimeTexts[i].setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        //    keyPressTimeTexts[i].setFill(Color.WHITE);
        //    PressTimeBox.getChildren().add(keyPressTimeTexts[i]);
        //}
        //
        //HBox deltaTimeBox = new HBox();
        //deltaTimeBox.setAlignment(Pos.CENTER);
        //Text[] keyDeltaTimeTexts = new Text[4];
        //for(int i = 0; i < 4; i++) {
        //    keyDeltaTimeTexts[i] = new Text(key[0] + "delta: " + 0);
        //    keyDeltaTimeTexts[i].setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        //    keyDeltaTimeTexts[i].setFill(Color.WHITE);
        //    deltaTimeBox.getChildren().add(keyDeltaTimeTexts[i]);
        //}
        //
        //HBox ReleaseTimeBox = new HBox();
        //ReleaseTimeBox.setAlignment(Pos.CENTER);
        //Text[] keyReleaseTimeTexts = new Text[4];
        //for(int i = 0; i < 4; i++) {
        //    keyReleaseTimeTexts[i] = new Text(key[0] + 0);
        //    keyReleaseTimeTexts[i].setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        //    keyReleaseTimeTexts[i].setFill(Color.WHITE);
        //    ReleaseTimeBox.getChildren().add(keyReleaseTimeTexts[i]);
        //}
        //
        //timeBox.getChildren().addAll(PressTimeBox, deltaTimeBox, ReleaseTimeBox);
        /*------按下按鍵後的時間資訊------*/

        /*
         * 按鍵按下
         */
        this.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.D) {
                Judge judgement = inputManager.handleKeyPress(KeyCode.D);
                trackPressedEffect[0].setVisible(true);
                //keyPressTimeTexts[0].setText("D: " + inputManager.getKeyPressTime(KeyCode.D));
                //keyDeltaTimeTexts[0].setText("D_deltaTime: " + inputManager.getDeltaTime(KeyCode.D));
                showJudgement(judgement);
            }
            if(e.getCode() == KeyCode.F) {
                Judge judgement = inputManager.handleKeyPress(KeyCode.F);
                trackPressedEffect[1].setVisible(true);
                //keyPressTimeTexts[1].setText("F: " + inputManager.getKeyPressTime(KeyCode.F));
                //keyDeltaTimeTexts[1].setText("F_deltaTime: " + inputManager.getDeltaTime(KeyCode.F));
                showJudgement(judgement);
            }
            if(e.getCode() == KeyCode.J) {
                Judge judgement = inputManager.handleKeyPress(KeyCode.J);
                trackPressedEffect[2].setVisible(true);
                //keyPressTimeTexts[2].setText("J: " + inputManager.getKeyPressTime(KeyCode.J));
                //keyDeltaTimeTexts[2].setText("J_deltaTime: " + inputManager.getDeltaTime(KeyCode.J));
                showJudgement(judgement);
            }
            if(e.getCode() == KeyCode.K) {
                Judge judgement = inputManager.handleKeyPress(KeyCode.K);
                trackPressedEffect[3].setVisible(true);
                //keyPressTimeTexts[3].setText("K: " + inputManager.getKeyPressTime(KeyCode.K));
                //keyDeltaTimeTexts[3].setText("K_deltaTime: " + inputManager.getDeltaTime(KeyCode.K));
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
                }
            }
            if(e.getCode() == KeyCode.P) {
                songPlayer.stop();
                gameLoop.stop();
                screenManager.switchToResultsScreen(background, readOsu);
            }
        });

        /*
         * 按鍵放開
         */
        this.setOnKeyReleased(e -> {
            if(e.getCode() == KeyCode.D) {
                Judge judgement = inputManager.handleKeyRelease(KeyCode.D);
                trackPressedEffect[0].setVisible(false);
                //keyReleaseTimeTexts[0].setText("D: " + inputManager.getKeyReleaseTime(KeyCode.D));
                showJudgement(judgement);
            }
            if(e.getCode() == KeyCode.F) {
                Judge judgement = inputManager.handleKeyRelease(KeyCode.F);
                trackPressedEffect[1].setVisible(false);
                //keyReleaseTimeTexts[1].setText("F: " + inputManager.getKeyReleaseTime(KeyCode.F));
                showJudgement(judgement);
            }
            if(e.getCode() == KeyCode.J) {
                Judge judgement = inputManager.handleKeyRelease(KeyCode.J);
                trackPressedEffect[2].setVisible(false);
                //keyReleaseTimeTexts[2].setText("J: " + inputManager.getKeyReleaseTime(KeyCode.J));
                showJudgement(judgement);
            }
            if(e.getCode() == KeyCode.K) {
                Judge judgement = inputManager.handleKeyRelease(KeyCode.K);
                trackPressedEffect[3].setVisible(false);
                //keyReleaseTimeTexts[3].setText("K: " + inputManager.getKeyReleaseTime(KeyCode.K));
                showJudgement(judgement);
            }
        });
    }

    /**
     * 更新遊戲
     *
     * @param gameTime 遊戲時間
     */
    private void update(int gameTime) {
        spawnNote(0, gameTime);
        spawnNote(1, gameTime);
        spawnNote(2, gameTime);
        spawnNote(3, gameTime);

        spawnHoldBody(0, gameTime);
        spawnHoldBody(1, gameTime);
        spawnHoldBody(2, gameTime);
        spawnHoldBody(3, gameTime);

        missNodeDetection(0, gameTime);
        missNodeDetection(1, gameTime);
        missNodeDetection(2, gameTime);
        missNodeDetection(3, gameTime);
    }

    /**
     * 產生音符
     *
     * @param trackIndex 音軌
     * @param gameTime 遊戲時間
     */
    private void spawnNote(int trackIndex, int gameTime) {
        List<Note> notes = beatMap.getTrack(trackIndex).getNotes();
        if(!notes.isEmpty()) {
            Note note = notes.get(0);
            if(gameTime > note.getBornTime()) {
                if(note instanceof Hold holdNote) {
                    if(holdNote.isStartNote()) {
                        isHolding[trackIndex] = true;
                    }else if(holdNote.isEndNote()) {
                        isHolding[trackIndex] = false;
                    }
                }
                noteFall(note, tracks[trackIndex], note.getDelayTime());
                getChildren().add(note);
                beatMap.getTrack(trackIndex).removeFrontNote();
                bornNotes.get(trackIndex).addNote(note);
            }
        }
    }

    private void spawnHoldBody(int trackIndex, int gameTime) {
        double interval = RhythmGame.defaultFlowTime * 2 / Settings.flowSpeed;
        if(isHolding[trackIndex]) {
            if((int) (gameTime % interval) == 0) {
                Hold holdBody = new Hold(trackIndex);
                noteFall(holdBody, tracks[trackIndex], holdBody.getDelayTime());
                getChildren().add(holdBody);
            }
        }
    }

    /**
     * MISS偵測
     *
     * @param trackIndex 音軌
     * @param gameTime 遊戲時間
     */
    private void missNodeDetection(int trackIndex, int gameTime) {
        if(!bornNotes.isEmpty()) {
            List<Note> notes = bornNotes.get(trackIndex).getNotes();
            if(!notes.isEmpty()) {
                Note frontNote = notes.get(0);
                if(gameTime > frontNote.getHitTime() + RhythmGame.acceptableRange) {
                    if(frontNote instanceof Single singleNote) {
                        getChildren().remove(singleNote);
                        singleNote.miss();
                        bornNotes.get(trackIndex).removeFrontNote();
                    }else if(frontNote instanceof Hold holdNote) {
                        if(holdNote.isEndNote()) {
                            getChildren().remove(holdNote);
                            holdNote.miss();
                            bornNotes.get(trackIndex).removeFrontNote();
                        }else if(holdNote.isStartNote()) {
                            getChildren().remove(holdNote);
                            holdNote.miss();
                            isHolding[trackIndex] = false;
                            bornNotes.get(trackIndex).removeFrontNote();
                            if(!notes.isEmpty()) {
                                getChildren().remove(notes.get(0));
                                bornNotes.get(trackIndex).removeFrontNote();
                            }else {
                                beatMap.getTrack(trackIndex).removeFrontNote();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 音符掉落動畫
     *
     * @param note 音符
     * @param track 音軌
     */
    private void noteFall(Note note, Line track, double delayTime) {
        Duration flowTime = Duration.millis(delayTime * 2 / Settings.flowSpeed);;
        PathTransition pathTransition = new PathTransition();
        pathTransition.setNode(note);
        pathTransition.setPath(track);
        pathTransition.setDuration(flowTime);
        pathTransition.setInterpolator(new NoteFallInterpolation());
        pathTransition.play();

        pathTransitions.add(pathTransition);

        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(note);
        scaleTransition.setDuration(flowTime);
        scaleTransition.setInterpolator(new NoteFallInterpolation());
        scaleTransition.setFromX(0.12);
        scaleTransition.setFromY(0.12);
        scaleTransition.setToX(1.88);
        scaleTransition.setToY(1.88);
        scaleTransition.play();

        scaleTransitions.add(scaleTransition);

        pathTransition.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            //if(newValue.toMillis() >= pathTransition.getTotalDuration().toMillis() / 2 + RhythmGame.acceptableRange) {
            //    note.setVisible(false);
            //}
            if(note instanceof Hold holdNote) {
                if(holdNote.isBodyNote()) {
                    if(newValue.toMillis() >= pathTransition.getTotalDuration().toMillis() / 2) {
                        getChildren().remove(note);
                    }
                }
                if(holdNote.isStartNote()) {
                    if(newValue.toMillis() >= pathTransition.getTotalDuration().toMillis() * 0.01) {
                        holdNote.toFront();
                    }
                }
            }
        });

        pathTransition.setOnFinished(e -> {
            getChildren().remove(note);

            pathTransitions.remove(pathTransition);
            scaleTransitions.remove(scaleTransition);
        });
    }

    /**
     * 設定背景
     */
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

    public ArrayList<Track> getBornNotes() {
        return bornNotes;
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

    /**
     * 離開視窗
     */
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
        }
    }

    public static void updateComboText(Judge judge) {
        if(judge == Judge.MISS) {
            comboText.setText("");
        }else {
            comboText.setText(String.valueOf(Judgement.combo));
        }

    }

    private void showJudgement(Judge judgement) {
        for(ImageView j : judgeEffect) {
            if(j.isVisible()) {
                return;
            }
        }
        int i = 0;
        PauseTransition hideJudgement = new PauseTransition(Duration.seconds(0.1));
        switch(judgement) {
            case PERFECT_PLUS:
                judgeEffect[0].setVisible(true);
                hideJudgement.play();
                break;
            case PERFECT:
                judgeEffect[1].setVisible(true);
                i = 1;
                hideJudgement.play();
                break;
            case Fast_GREAT, Late_GREAT:
                judgeEffect[2].setVisible(true);
                i = 2;
                hideJudgement.play();
                break;
            case Fast_GOOD, Late_GOOD:
                judgeEffect[3].setVisible(true);
                i = 3;
                hideJudgement.play();
                break;
            case Fast_BAD, Late_BAD:
                judgeEffect[4].setVisible(true);
                i = 4;
                hideJudgement.play();
                break;
            case NONE:
                break;
        }
        int finalI = i;
        hideJudgement.setOnFinished(f -> judgeEffect[finalI].setVisible(false));
    }
}
