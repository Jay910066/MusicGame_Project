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
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * 遊戲畫面
 */
public class GamePlay extends Pane {
    private final ScreenManager screenManager; //畫面管理器
    private final File selectedSong; //選擇的歌曲
    private ImageView background; //背景圖片
    private ImageView PlayField; //遊戲地圖
    private HBox leaveWindow; //離開視窗
    private final BeatMap beatMap; //譜面
    private final Line[] tracks = new Line[4]; //軌道
    private final ArrayList<Track> renderedNotes; //已渲染的音符
    private final boolean[] isHolding = new boolean[4]; //是否處於長按狀態
    private final MediaPlayer songPlayer; //音樂播放器
    private PauseTransition introWait; //開始遊戲前的等待
    private PauseTransition outroWait; //遊戲結束後的等待
    private long startTime; //遊戲開始時間
    private long pauseTime; //遊戲暫停時間
    private boolean isPaused = false; //遊戲是否暫停
    private AnimationTimer gameLoop; //遊戲迴圈
    private static Text comboText; //Combo顯示
    private static Text ScoreText;
    private final List<PathTransition> notePathTransitions = new ArrayList<>(); //音符掉落動畫
    private final List<ScaleTransition> noteScaleTransitions = new ArrayList<>(); //音符放大動畫
    private final ImageView[] trackPressedEffect = new ImageView[4]; //軌道按下特效
    private final ImageView[] hitEffect = new ImageView[4]; //擊中特效
    private static ScaleTransition comboTextScaleTransition; //Combo放大動畫
    private final ScaleTransition[] hitEffectScaleTransition = new ScaleTransition[4]; //擊中特效放大動畫
    private final PauseTransition[] hideHitEffectPauseTransition = new PauseTransition[4]; //隱藏擊中特效的等待
    private final ImageView[] judgeEffect = new ImageView[5]; //評價特效

    private Text gameTimer = new Text("Time: " + 0); //遊戲時間
    private Text[] keyPressTimeTexts = new Text[4]; //按下按鍵的時間
    private Text[] keyDeltaTimeTexts = new Text[4]; //擊中後的時間差
    private Text[] keyReleaseTimeTexts = new Text[4]; //放開按鍵的時間

    private final double centerX; //畫面中心X座標
    private final double centerY; //畫面中心Y座標
    private static int MAX_WIDTH = 1920; //畫面寬度
    private static int MAX_HEIGHT = 1050; //畫面高度

    /**
     * 遊戲畫面
     *
     * @param screenManager 畫面管理器
     * @param selectedSong 選擇的歌曲
     */
    public GamePlay(ScreenManager screenManager, File selectedSong) {
        //初始化
        this.screenManager = screenManager;
        this.selectedSong = selectedSong;
        centerX = MAX_WIDTH / 2;
        centerY = MAX_HEIGHT / 2;
        this.setFocusTraversable(true);
        this.requestFocus();

        //設定背景、PlayField、軌道、Combo顯示、評價等特效
        setBackground();
        setPlayField();
        setTrackPath();
        setTrackPressedEffect();
        setHitEffect();
        setComboText();
        setScoreText();
        setJudgeEffect();
        setLeaveWindow();

        //讀取歌曲資訊，取得BeatMap
        ReadOsu readOsu = new ReadOsu(selectedSong.getPath() + "/Info.osu");
        beatMap = readOsu.getBeatMap();

        renderedNotes = new ArrayList<>(4);
        for(int i = 0; i < 4; i++) {
            renderedNotes.add(new Track());
        }

        for(int i = 0; i < 4; i++) {
            isHolding[i] = false;
        }

        //設定音樂
        Media playSong = new Media(new File(selectedSong, "song.mp3").toURI().toString());
        songPlayer = new MediaPlayer(playSong);

        //音樂準備完畢，開始遊戲
        songPlayer.setOnReady(() -> {
            Judgement.reset(); //重置評價
            songPlayer.setVolume(Settings.volume / 100.0); //設定音量
            introWait = new PauseTransition(Duration.seconds(2)); //等待2秒後開始遊戲

            //等待結束，開始遊戲
            introWait.setOnFinished(e -> {
                songPlayer.play(); //播放音樂
                startTime = System.nanoTime(); //開始時間

                //遊戲迴圈
                gameLoop = new AnimationTimer() {

                    @Override
                    //每一幀更新遊戲
                    public void handle(long now) {
                        //如果遊戲暫停，則不更新遊戲
                        if(isPaused) {
                            return;
                        }

                        //更新遊戲時間
                        int gameTime = (int) ((now - startTime) / 1_000_000);
                        update(gameTime);
                    }
                };
                gameLoop.start(); //開始遊戲迴圈
            });
            introWait.play(); //開始等待
        });

        //音樂播放完畢，切換到結果畫面
        songPlayer.setOnEndOfMedia(() -> {
            gameLoop.stop(); //停止遊戲迴圈
            outroWait = new PauseTransition(Duration.seconds(2)); //等待2秒後切換到結果畫面

            //等待結束，切換到結果畫面
            outroWait.setOnFinished(e -> {
                screenManager.switchToResultsScreen(background, readOsu);
            });
            outroWait.play(); //開始等待
        });

        //輸入管理器
        InputManager inputManager = new InputManager(this);

        /*
         * 按鍵按下
         * 判斷按下的按鍵，並處理
         * 按下按鍵後，顯示按下特效
         * 顯示擊中特效
         * 顯示評價
         */
        this.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.D) {
                Judge judgement = inputManager.handleKeyPress(KeyCode.D);
                trackPressedEffect[0].setVisible(true);
                showHitEffect(0, judgement);
                showJudgement(judgement);
                //keyPressTimeTexts[0].setText("D: " + inputManager.getKeyPressTime(KeyCode.D));
                //keyDeltaTimeTexts[0].setText("D_deltaTime: " + inputManager.getDeltaTime(KeyCode.D));
            }
            if(e.getCode() == KeyCode.F) {
                Judge judgement = inputManager.handleKeyPress(KeyCode.F);
                trackPressedEffect[1].setVisible(true);
                showHitEffect(1, judgement);
                showJudgement(judgement);
                //keyPressTimeTexts[1].setText("F: " + inputManager.getKeyPressTime(KeyCode.F));
                //keyDeltaTimeTexts[1].setText("F_deltaTime: " + inputManager.getDeltaTime(KeyCode.F));
            }
            if(e.getCode() == KeyCode.J) {
                Judge judgement = inputManager.handleKeyPress(KeyCode.J);
                trackPressedEffect[2].setVisible(true);
                showHitEffect(2, judgement);
                showJudgement(judgement);
                //keyPressTimeTexts[2].setText("J: " + inputManager.getKeyPressTime(KeyCode.J));
                //keyDeltaTimeTexts[2].setText("J_deltaTime: " + inputManager.getDeltaTime(KeyCode.J));
            }
            if(e.getCode() == KeyCode.K) {
                Judge judgement = inputManager.handleKeyPress(KeyCode.K);
                trackPressedEffect[3].setVisible(true);
                showHitEffect(3, judgement);
                showJudgement(judgement);
                //keyPressTimeTexts[3].setText("K: " + inputManager.getKeyPressTime(KeyCode.K));
                //keyDeltaTimeTexts[3].setText("K_deltaTime: " + inputManager.getDeltaTime(KeyCode.K));
            }

            //按下ESC，顯示離開視窗
            if(e.getCode() == KeyCode.ESCAPE) {
                if(!getChildren().contains(leaveWindow)) {
                    getChildren().add(leaveWindow);
                    songPlayer.pause();
                    introWait.pause();
                    if(outroWait != null) {
                        outroWait.pause();
                    }
                    isPaused = true;
                    for(PathTransition pathTransition : notePathTransitions) {
                        pathTransition.pause();
                    }
                    for(ScaleTransition scaleTransition : noteScaleTransitions) {
                        scaleTransition.pause();
                    }
                    pauseTime = System.nanoTime();
                }
            }

            //按下P，暫停遊戲，切換到結果畫面
            if(e.getCode() == KeyCode.P) {
                if(songPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    songPlayer.stop();
                    gameLoop.stop();
                }
                screenManager.switchToResultsScreen(background, readOsu);
            }
        });

        /*
         * 按鍵放開
         * 判斷放開的按鍵，並處理
         * 隱藏按下特效
         * 顯示擊中特效
         * 顯示評價
         */
        this.setOnKeyReleased(e -> {
            if(e.getCode() == KeyCode.D) {
                Judge judgement = inputManager.handleKeyRelease(KeyCode.D);
                trackPressedEffect[0].setVisible(false);
                showHitEffect(0, judgement);
                showJudgement(judgement);
                //keyReleaseTimeTexts[0].setText("D: " + inputManager.getKeyReleaseTime(KeyCode.D));
            }
            if(e.getCode() == KeyCode.F) {
                Judge judgement = inputManager.handleKeyRelease(KeyCode.F);
                trackPressedEffect[1].setVisible(false);
                showHitEffect(1, judgement);
                showJudgement(judgement);
                //keyReleaseTimeTexts[1].setText("F: " + inputManager.getKeyReleaseTime(KeyCode.F));
            }
            if(e.getCode() == KeyCode.J) {
                Judge judgement = inputManager.handleKeyRelease(KeyCode.J);
                trackPressedEffect[2].setVisible(false);
                showHitEffect(2, judgement);
                showJudgement(judgement);
                //keyReleaseTimeTexts[2].setText("J: " + inputManager.getKeyReleaseTime(KeyCode.J));
            }
            if(e.getCode() == KeyCode.K) {
                Judge judgement = inputManager.handleKeyRelease(KeyCode.K);
                trackPressedEffect[3].setVisible(false);
                showHitEffect(3, judgement);
                showJudgement(judgement);
                //keyReleaseTimeTexts[3].setText("K: " + inputManager.getKeyReleaseTime(KeyCode.K));
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
     * 在對應Index的音軌上產生音符
     *
     * @param trackIndex 音軌
     * @param gameTime 遊戲時間
     */
    private void spawnNote(int trackIndex, int gameTime) {
        //取得該軌道的音符
        Queue<Note> notes = beatMap.getTrack(trackIndex).getNotes();

        //如果音符不為空
        if(!notes.isEmpty()) {
            Note note = notes.peek();
            //如果遊戲時間大於音符的出現時間，則產生音符
            if(gameTime >= note.getBornTime()) {
                //如果是長按音符，則判定是StartNote還是EndNote
                if(note instanceof Hold holdNote) {
                    //如果是StartNote，則設定該軌道為長按狀態
                    if(holdNote.isStartNote()) {
                        isHolding[trackIndex] = true;
                    }
                    //如果是BodyNote，則關閉該軌道的長按狀態
                    else if(holdNote.isEndNote()) {
                        isHolding[trackIndex] = false;
                    }
                }
                //音符掉落
                noteFall(note, tracks[trackIndex], trackIndex, note.getDelayTime());
                getChildren().add(note);
                beatMap.getTrack(trackIndex).removeFrontNote();
                renderedNotes.get(trackIndex).addNote(note);
            }
        }
    }

    /**
     * 產生長按音符的BodyNote
     * 透過持續產生BodyNote的方式，形成長按音符的中間部分
     *
     * @param trackIndex 音軌
     * @param gameTime 遊戲時間
     */
    private void spawnHoldBody(int trackIndex, int gameTime) {
        //BodyNote的產生間隔
        double interval = RhythmGame.defaultFlowTime * 2 / Settings.flowSpeed;

        //如果該軌道正在長按
        if(isHolding[trackIndex]) {
            //每隔一段時間產生一個BodyNote
            if((int) (gameTime % interval) == 0) {
                Hold holdBody = new Hold(trackIndex);
                noteFall(holdBody, tracks[trackIndex], trackIndex, holdBody.getDelayTime());
                getChildren().add(holdBody);
            }
        }
    }

    /**
     * 偵測該Index的音軌是否有MISS
     *
     * @param trackIndex 音軌Index
     * @param gameTime 遊戲時間
     */
    private void missNodeDetection(int trackIndex, int gameTime) {
        //如果已渲染音符不為空
        if(!renderedNotes.isEmpty()) {
            //取得該軌道的音符
            Queue<Note> notes = renderedNotes.get(trackIndex).getNotes();
            //如果該軌道的音符不為空
            if(!notes.isEmpty()){
                Note frontNote = notes.peek(); //取得最前面的音符

                //如果音符在判定時間的誤差範圍外，則視為MISS
                if(gameTime > frontNote.getHitTime() + RhythmGame.acceptableRange) {

                    //如果音符是單點音符，則直接MISS
                    if(frontNote instanceof Single singleNote) {
                        getChildren().remove(singleNote);
                        singleNote.miss();
                        renderedNotes.get(trackIndex).removeFrontNote();
                    }
                    //如果音符是長按音符
                    else if(frontNote instanceof Hold holdNote) {
                        //如果是EndNote，則直接MISS
                        if(holdNote.isEndNote()) {
                            getChildren().remove(holdNote);
                            holdNote.miss();
                            renderedNotes.get(trackIndex).removeFrontNote();
                        }
                        //如果是StartNote，則判定為MISS，並移除StartNote和停止渲染BodyNote
                        else if(holdNote.isStartNote()) {
                            getChildren().remove(holdNote);
                            holdNote.miss();
                            isHolding[trackIndex] = false;
                            renderedNotes.get(trackIndex).removeFrontNote();

                            //若該軌道以渲染的音符不為空，則最前面的音符必為EndNote，移除EndNote
                            if(!notes.isEmpty()) {
                                getChildren().remove(notes.peek());
                                renderedNotes.get(trackIndex).getNotes().peek().miss();
                                renderedNotes.get(trackIndex).removeFrontNote();
                            }
                            //若沒有已渲染的音符，則從BeatMap中移除EndNote
                            else {
                                beatMap.getTrack(trackIndex).getNotes().peek().miss();
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
    private void noteFall(Note note, Line track, int trackIndex, double delayTime) {

        //音符掉落時間，根據設定的速度調整
        Duration flowTime = Duration.millis(delayTime * 2 / Settings.flowSpeed);

        //音符掉落動畫，根據音軌調整插值
        PathTransition pathTransition = new PathTransition();
        pathTransition.setNode(note);
        pathTransition.setPath(track);
        pathTransition.setDuration(flowTime);
        if(trackIndex == 0 || trackIndex == 3) {
            pathTransition.setInterpolator(new NoteFallInterpolation(1));
        }else {
            pathTransition.setInterpolator(new NoteFallInterpolation(0));
        }
        pathTransition.play();

        notePathTransitions.add(pathTransition);

        //音符放大動畫，根據音軌調整插值
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(note);
        scaleTransition.setDuration(flowTime);
        if(trackIndex == 0 || trackIndex == 3) {
            scaleTransition.setInterpolator(new NoteFallInterpolation(1));
        }else {
            scaleTransition.setInterpolator(new NoteFallInterpolation(0));
        }
        scaleTransition.setFromX(0.12);
        scaleTransition.setFromY(0.12);
        scaleTransition.setToX(1.88);
        scaleTransition.setToY(1.88);
        scaleTransition.play();

        noteScaleTransitions.add(scaleTransition);

        //長按音符掉落動畫控制
        pathTransition.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if(note instanceof Hold holdNote) {
                //長按音符的BodyNote在判定線處消失
                if(holdNote.isBodyNote()) {
                    if(newValue.toMillis() >= pathTransition.getTotalDuration().toMillis() / 2) {
                        getChildren().remove(note);
                    }
                }
                //長按音符的StartNote在渲染後一小段時間前移到最上層，使其在BodyNote之上
                if(holdNote.isStartNote()) {
                    if(newValue.toMillis() >= pathTransition.getTotalDuration().toMillis() * 0.02) {
                        holdNote.toFront();
                    }
                }
            }
        });

        pathTransition.setOnFinished(e -> {
            getChildren().remove(note);

            notePathTransitions.remove(pathTransition);
            noteScaleTransitions.remove(scaleTransition);
        });
    }

    /**
     * 設定背景
     */
    private void setBackground() {
        background = new ImageView();
        getChildren().add(background);

        //調整背景亮度
        ColorAdjust brightness = new ColorAdjust();
        brightness.setBrightness(-0.5);
        background.setEffect(brightness);

        //設定背景大小，使背景填滿螢幕
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double maxDimension = Math.max(screenBounds.getWidth(), screenBounds.getHeight());
        background.setPreserveRatio(true);
        if(background.getFitHeight() < background.getFitWidth())
            background.setFitHeight(maxDimension * 1);
        else
            background.setFitWidth(maxDimension * 1);

        //設定背景圖片，如果有background.jpg則使用background.jpg，否則使用cover.jpg
        if(new File(selectedSong.getPath() + "/background.jpg").exists()) {
            background.setImage(new Image("file:" + selectedSong.getPath() + "/background.jpg"));
        }else {
            background.setImage(new Image("file:" + selectedSong.getPath() + "/cover.jpg"));
        }
    }

    /**
     * 設置軌道圖片
     */
    private void setPlayField(){
        PlayField = new ImageView(new Image("file:Resources/Images/PlayField.png"));
        getChildren().add(PlayField);
        PlayField.setX(centerX - (PlayField.getBoundsInParent().getWidth() / 2) - 300);
        PlayField.setY(centerY - (PlayField.getBoundsInParent().getHeight() / 2));
    }

    /**
     * 設置軌道
     */
    private void setTrackPath(){
        tracks[0] = new Line(620, 110, -180, 940);
        tracks[1] = new Line(642.684, 135, 257.316, 1615);
        tracks[2] = new Line(677.316, 135, 1062.684, 1615);
        tracks[3] = new Line(700, 110, 1500, 940);
        //getChildren().addAll(tracks);
    }

    /**
     * 設置軌道按下特效
     */
    private void setTrackPressedEffect(){
        for(int i = 0; i < 4; i++) {
            trackPressedEffect[i] = new ImageView(new Image("file:Resources/Images/Track" + (i + 1) + "_Pressed.png"));
            trackPressedEffect[i].setX(centerX - (PlayField.getBoundsInParent().getWidth() / 2) - 300);
            trackPressedEffect[i].setY(centerY - (PlayField.getBoundsInParent().getHeight() / 2));
            trackPressedEffect[i].setOpacity(0.5);
            trackPressedEffect[i].setVisible(false);
            getChildren().add(trackPressedEffect[i]);
        }
    }

    /**
     * 設置Combo顯示
     */
    private void setComboText(){
        comboText = new Text();
        comboText.setWrappingWidth(200);
        comboText.setTextAlignment(TextAlignment.CENTER);
        comboText.setStyle("-fx-font-size: 56px; -fx-font-weight: bold;");
        comboText.setFill(Color.WHITE);
        comboText.setX(centerX + 150);
        comboText.setY(centerY - 250);
        comboText.setEffect(new Glow(1));
        getChildren().add(comboText);
    }
    /**
     * 設置Score顯示
     */
    private void setScoreText(){
        ScoreText = new Text();
        ScoreText.setWrappingWidth(200);
        ScoreText.setTextAlignment(TextAlignment.CENTER);
        ScoreText.setStyle("-fx-font-size: 56px; -fx-font-weight: bold;");
        ScoreText.setFill(Color.WHITE);
        ScoreText.setX(centerX - 650);
        ScoreText.setY(centerY - 400);
        getChildren().add(ScoreText);
    }
    /**
     * 設置評價特效
     */
    private void setJudgeEffect(){
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
    }

    /**
     * 設置時間資訊
     * 顯示遊戲時間、按下按鍵、放開按鍵及擊中後的誤差時間的資訊
     */
    private void setTimeInfo(){
        //遊戲時間
        gameTimer = new Text("Time: " + 0);
        gameTimer.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        gameTimer.setLayoutX(centerX - 100);
        gameTimer.setLayoutY(centerY - 300);
        getChildren().add(gameTimer);


        /*------按下按鍵後的時間資訊------*/

        //存放時間資訊
        VBox timeBox = new VBox();
        getChildren().add(timeBox);
        timeBox.setLayoutX(centerX - 200);
        timeBox.setLayoutY(centerY - 300);
        timeBox.setAlignment(Pos.CENTER);

        //按下按鍵的時間
        HBox PressTimeBox = new HBox();
        PressTimeBox.setAlignment(Pos.CENTER);
        String[] key = {"D: ", "F: ", "J: ", "K: "};
        keyPressTimeTexts = new Text[4];
        for(int i = 0; i < 4; i++) {
            keyPressTimeTexts[i] = new Text(key[0] + 0);
            keyPressTimeTexts[i].setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            keyPressTimeTexts[i].setFill(Color.WHITE);
            PressTimeBox.getChildren().add(keyPressTimeTexts[i]);
        }

        //擊中後的時間差
        HBox deltaTimeBox = new HBox();
        deltaTimeBox.setAlignment(Pos.CENTER);
        keyDeltaTimeTexts = new Text[4];
        for(int i = 0; i < 4; i++) {
            keyDeltaTimeTexts[i] = new Text(key[0] + "delta: " + 0);
            keyDeltaTimeTexts[i].setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            keyDeltaTimeTexts[i].setFill(Color.WHITE);
            deltaTimeBox.getChildren().add(keyDeltaTimeTexts[i]);
        }

        //放開按鍵的時間
        HBox ReleaseTimeBox = new HBox();
        ReleaseTimeBox.setAlignment(Pos.CENTER);
        keyReleaseTimeTexts = new Text[4];
        for(int i = 0; i < 4; i++) {
            keyReleaseTimeTexts[i] = new Text(key[0] + 0);
            keyReleaseTimeTexts[i].setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            keyReleaseTimeTexts[i].setFill(Color.WHITE);
            ReleaseTimeBox.getChildren().add(keyReleaseTimeTexts[i]);
        }

        timeBox.getChildren().addAll(PressTimeBox, deltaTimeBox, ReleaseTimeBox);
        /*------按下按鍵後的時間資訊------*/
    }

    /**
     * 設置離開視窗
     */
    private void setLeaveWindow() {
        //離開視窗
        leaveWindow = new HBox();
        leaveWindow.setAlignment(Pos.CENTER);
        leaveWindow.setSpacing(40);
        leaveWindow.setLayoutX(centerX - 400);
        leaveWindow.setLayoutY(centerY - 300);
        leaveWindow.setMinWidth(800);
        leaveWindow.setMinHeight(600);
        leaveWindow.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        //離開視窗訊息
        Label message = new Label("Are you sure you want to exit?");
        message.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        //離開視窗按鈕
        Button confirmButton = new Button("Yes");
        confirmButton.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        confirmButton.setOnAction(event -> {
            introWait.stop();
            if(outroWait != null) {
                outroWait.stop();
            }
            screenManager.switchToSongListMenu();
        });

        //重試按鈕
        Button retryButton = new Button("Retry");
        retryButton.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        retryButton.setOnAction(event -> {
            getChildren().remove(leaveWindow);
            screenManager.switchToGamePlay(selectedSong);
        });

        //繼續按鈕
        Button continueButton = new Button("Continue");
        continueButton.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        continueButton.setOnAction(event -> {
            getChildren().remove(leaveWindow);

            //繼續播放音樂
            if(songPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                songPlayer.play();
            }

            //繼續遊戲時間
            if(introWait.getStatus() == Animation.Status.PAUSED) {
                introWait.play();
            }

            //繼續歌曲結束後的等待
            if(outroWait != null) {
                outroWait.play();
            }

            isPaused = false; //取消暫停
            startTime += System.nanoTime() - pauseTime; //更新開始時間

            //繼續音符掉落動畫
            for(PathTransition pathTransition : notePathTransitions) {
                pathTransition.play();
            }
            for(ScaleTransition scaleTransition : noteScaleTransitions) {
                scaleTransition.play();
            }
        });

        leaveWindow.getChildren().addAll(message, confirmButton, retryButton, continueButton);
    }

    /**
     * 更新Combo數字
     *
     * @param judge 評價
     */
    public static void updateComboText(Judge judge) {
        if(judge == Judge.MISS) {
            comboText.setText("");
        }else {
            comboText.setText(String.valueOf(Judgement.combo));

            if(comboTextScaleTransition != null) {
                comboTextScaleTransition.stop();
            }

            //Combo放大動畫
            comboTextScaleTransition = new ScaleTransition(Duration.seconds(0.1), comboText);
            comboTextScaleTransition.setFromX(0.7);
            comboTextScaleTransition.setFromY(0.7);
            comboTextScaleTransition.setToX(1);
            comboTextScaleTransition.setToY(1);
            comboTextScaleTransition.play();
        }

    }

    /**
     * 更新分數數字
     *
     */
    public static void updateScoreText() {
        ScoreText.setText(String.valueOf(Judgement.score));
    }
    /**
     * 設定打擊特效
     */
    private void setHitEffect() {
        for(int i = 0; i < 4; i++) {
            hitEffect[i] = new ImageView(new Image("file:Resources/Images/lighting.png"));
            hitEffect[i].setVisible(false);
            getChildren().add(hitEffect[i]);
        }
        hitEffect[0].setLayoutX(115);
        hitEffect[0].setLayoutY(420);
        hitEffect[0].setRotate(90);
        hitEffect[1].setLayoutX(345);
        hitEffect[1].setLayoutY(770);
        hitEffect[2].setLayoutX(765);
        hitEffect[2].setLayoutY(770);
        hitEffect[3].setLayoutX(995);
        hitEffect[3].setLayoutY(420);
        hitEffect[3].setRotate(270);
    }

    /**
     * 顯示打擊特效
     *
     * @param trackIndex 音軌
     * @param judge 評價
     */
    private void showHitEffect(int trackIndex, Judge judge) {

        //如果評價不是MISS或NONE，則顯示打擊特效
        if(judge != Judge.NONE && judge != Judge.MISS) {
            hitEffect[trackIndex].setVisible(true);
            hitEffect[trackIndex].toFront();

            //停止之前的特效動畫
            if(hitEffectScaleTransition[trackIndex] != null) {
                hitEffectScaleTransition[trackIndex].stop();
            }
            if(hideHitEffectPauseTransition[trackIndex] != null) {
                hideHitEffectPauseTransition[trackIndex].stop();
            }

            //播放特效動畫
            hitEffectScaleTransition[trackIndex] = new ScaleTransition(Duration.seconds(0.1), hitEffect[trackIndex]);
            hitEffectScaleTransition[trackIndex].setFromX(0.1);
            hitEffectScaleTransition[trackIndex].setFromY(0.1);
            hitEffectScaleTransition[trackIndex].setToX(1);
            hitEffectScaleTransition[trackIndex].setToY(1);
            hitEffectScaleTransition[trackIndex].play();

            hideHitEffectPauseTransition[trackIndex] = new PauseTransition(Duration.seconds(0.1));
            hideHitEffectPauseTransition[trackIndex].setOnFinished(e -> hitEffect[trackIndex].setVisible(false));
            hideHitEffectPauseTransition[trackIndex].play();
        }
    }

    /**
     * 顯示評價
     *
     * @param judgement 評價
     */
    private void showJudgement(Judge judgement) {
        for(ImageView j : judgeEffect) {
            if(j.isVisible()) {
                return;
            }
        }
        int i = 0;

        //顯示評價
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

    /**
     * 取得已渲染音符
     *
     * @return 已渲染音符
     */
    public ArrayList<Track> getRenderedNotes() {
        return renderedNotes;
    }

    /**
     * 取得譜面
     *
     * @return 譜面
     */
    public BeatMap getBeatMap() {
        return beatMap;
    }

    /**
     * 取得是否處於長按狀態
     */
    public boolean[] getIsHolding() {
        return isHolding;
    }

    /**
     * 取得開始時間
     *
     * @return 開始時間
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * 音符下落動畫插值
     */
    public static class NoteFallInterpolation extends Interpolator {
        int i;

        NoteFallInterpolation(int i) {
            this.i = i;
        }

        @Override
        protected double curve(double t) {
            return (-t - 0.033 * i) / (2 * t + 0.066 * i - 2);
        }//former:2*t*t
    }
}
