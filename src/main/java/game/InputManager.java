package game;

import javafx.scene.input.KeyCode;

import java.util.*;

/**
 * 輸入管理器
 * 處理玩家輸入
 */
public class InputManager {
    private final GamePlay gamePlay; //遊戲畫面
    private final SoundEffect[] soundEffects = new SoundEffect[4]; //音效
    private final Set<KeyCode> currentlyPressedKeys = new HashSet<>(); //目前按下的按鍵
    private final Map<KeyCode, Integer> keyPressTimes = new HashMap<>(); //按下按鍵的時間
    private final Map<KeyCode, Integer> deltaTime = new HashMap<>(); //按下按鍵與擊中音符的時間差
    private final Map<KeyCode, Integer> keyReleaseTimes = new HashMap<>(); //放開按鍵的時間

    /**
     * 建構子
     * @param gamePlay 遊戲畫面
     */
    public InputManager(GamePlay gamePlay) {
        //初始化
        this.gamePlay = gamePlay;
        for(int i = 0; i < 4; i++) {
            soundEffects[i] = new SoundEffect();
        }
        for(KeyCode keyCode : Arrays.asList(KeyCode.D, KeyCode.F, KeyCode.J, KeyCode.K)) {
            keyPressTimes.put(keyCode, 0);
            deltaTime.put(keyCode, 0);
            keyReleaseTimes.put(keyCode, 0);
        }
    }

    /**
     * 處理按下按鍵
     * @param keyCode 按鍵
     * @return 評價等級
     */
    public Judge handleKeyPress(KeyCode keyCode) {
        //如果按鍵已經按下，則不處理
        if(currentlyPressedKeys.contains(keyCode)) {
            return Judge.NONE;
        }
        currentlyPressedKeys.add(keyCode);

        //取得軌道索引
        int trackIndex = getTrackIndex(keyCode);

        //如果軌道索引在範圍內
        if(trackIndex != -1) {
            //更新按下按鍵的時間
            keyPressTimes.put(keyCode, (int) ((System.nanoTime() - gamePlay.getStartTime()) / 1_000_000));

            //取得軌道的音符
            Queue<Note> notes = gamePlay.getRenderedNotes().get(trackIndex).getNotes();

            //如果音符不為空
            if(!notes.isEmpty()) {
                //取得音符
                Note note = gamePlay.getRenderedNotes().get(trackIndex).getNotes().peek();
                //檢查是否擊中音符
                Judge judge = note.OnHitCheck(keyPressTimes.get(keyCode));

                //如果是單點音符
                if(note instanceof Single singleNote) {
                    //如果擊中音符
                    if(judge != Judge.NONE) {
                        Judgement.judge(judge);
                        deltaTime.put(keyCode, singleNote.getHitTime() - keyPressTimes.get(keyCode));
                        gamePlay.getChildren().remove(singleNote);
                        gamePlay.getRenderedNotes().get(trackIndex).removeFrontNote();
                        soundEffects[trackIndex].playHitSound();
                        return judge;
                    }
                }
                //如果是長按音符
                else if(note instanceof Hold holdNote) {
                    //如果擊中音符且是開始音符
                    if(judge != Judge.NONE && holdNote.isStartNote()) {
                        Judgement.judge(judge);
                        deltaTime.put(keyCode, holdNote.getHitTime() - keyPressTimes.get(keyCode));
                        gamePlay.getChildren().remove(holdNote);
                        gamePlay.getRenderedNotes().get(trackIndex).removeFrontNote();
                        soundEffects[trackIndex].playHitSound();
                        return judge;
                    }
                }

                //如果空打，播放空打音效
                if(judge == Judge.NONE) {
                    soundEffects[trackIndex].playTapSound();
                }
            }
            soundEffects[trackIndex].playTapSound();
        }
        return Judge.NONE;
    }

    /**
     * 處理放開按鍵
     * @param keyCode 按鍵
     * @return 評價等級
     */
    public Judge handleKeyRelease(KeyCode keyCode) {
        //將按鍵從目前按下的按鍵中移除
        currentlyPressedKeys.remove(keyCode);

        //取得軌道索引
        int trackIndex = getTrackIndex(keyCode);

        //如果軌道索引在範圍內
        if(trackIndex != -1) {
            //更新放開按鍵的時間
            keyReleaseTimes.put(keyCode, (int) ((System.nanoTime() - gamePlay.getStartTime()) / 1_000_000));
            //取得已渲染軌道的音符
            Queue<Note> notes = gamePlay.getRenderedNotes().get(trackIndex).getNotes();

            //如果已渲染音符不為空
            if(!notes.isEmpty()) {
                //取得最前面的音符
                Note note = gamePlay.getRenderedNotes().get(trackIndex).getNotes().peek();
                //檢查是否擊中音符
                Judge judge = note.OnHitCheck(keyReleaseTimes.get(keyCode));

                //如果是長按音符，且是結束音符
                if(note instanceof Hold holdNote && holdNote.isEndNote()) {
                    //如果擊中音符
                    if(judge != Judge.NONE) {
                        Judgement.judge(judge);
                        deltaTime.put(keyCode, holdNote.getHitTime() - keyReleaseTimes.get(keyCode));
                        gamePlay.getChildren().remove(holdNote);
                        gamePlay.getRenderedNotes().get(trackIndex).removeFrontNote();
                        return judge;
                    }
                    //如果結束音符還未進入擊中範圍，則視為MISS
                    else {
                        holdNote.miss();
                        gamePlay.getChildren().remove(holdNote);
                        gamePlay.getRenderedNotes().get(trackIndex).removeFrontNote();
                        return Judge.MISS;
                    }
                }
            }
            //如果已渲染音符為空，則刪除譜面中該軌道最前面的音符，視為MISS
            else if(!gamePlay.getBeatMap().getTrack(trackIndex).getNotes().isEmpty()) {
                //取得該軌道中最前面的音符
                Note note = gamePlay.getBeatMap().getTrack(trackIndex).getNotes().peek();

                //如果是長按音符，且是結束音符
                if(note instanceof Hold holdNote && holdNote.isEndNote()) {
                    holdNote.miss();
                    gamePlay.getBeatMap().getTrack(trackIndex).removeFrontNote();
                    return Judge.MISS;
                }
            }
        }
        return Judge.NONE;
    }

    /**
     * 取得軌道索引
     * @param keyCode 按鍵
     * @return 軌道索引
     */
    private int getTrackIndex(KeyCode keyCode) {
        switch(keyCode) {
            case D:
                return 0;
            case F:
                return 1;
            case J:
                return 2;
            case K:
                return 3;
            default:
                return -1;
        }
    }

    /**
     * 取得按下按鍵的時間
     * @param keyCode 按鍵
     * @return 按下按鍵的時間
     */
    public int getKeyPressTime(KeyCode keyCode) {
        return keyPressTimes.getOrDefault(keyCode, 0);
    }

    /**
     * 取得按下按鍵與擊中音符的時間差
     * @param keyCode 按鍵
     * @return 時間差
     */
    public int getDeltaTime(KeyCode keyCode) {
        return deltaTime.getOrDefault(keyCode, 0);
    }

    /**
     * 取得放開按鍵的時間
     * @param keyCode 按鍵
     * @return 放開按鍵的時間
     */
    public int getKeyReleaseTime(KeyCode keyCode) {
        return keyReleaseTimes.getOrDefault(keyCode, 0);
    }
}
