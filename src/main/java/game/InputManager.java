package game;

import javafx.scene.input.KeyCode;

import java.util.*;

public class InputManager {
    private GamePlay gamePlay;
    private Set<KeyCode> currentlyPressedKeys = new HashSet<>();
    private Map<KeyCode, Integer> keyPressTimes = new HashMap<>();
    private Map<KeyCode, Integer> deltaTime = new HashMap<>();
    private Map<KeyCode, Integer> keyReleaseTimes = new HashMap<>();

    public InputManager(GamePlay gamePlay) {
        this.gamePlay = gamePlay;
        for(KeyCode keyCode : Arrays.asList(KeyCode.D, KeyCode.F, KeyCode.J, KeyCode.K)) {
            keyPressTimes.put(keyCode, 0);
            deltaTime.put(keyCode, 0);
            keyReleaseTimes.put(keyCode, 0);
        }
    }

    public Judge handleKeyPress(KeyCode keyCode) {
        if(currentlyPressedKeys.contains(keyCode)) {
            return Judge.NONE;
        }
        currentlyPressedKeys.add(keyCode);

        int trackIndex = getTrackIndex(keyCode);
        if(trackIndex != -1) {
            keyPressTimes.put(keyCode, (int) ((System.nanoTime() - gamePlay.getStartTime()) / 1_000_000));
            List<Note> notes = gamePlay.getBornNotes().get(trackIndex).getNotes();
            if(!notes.isEmpty()) {
                Note note = gamePlay.getBornNotes().get(trackIndex).getNotes().get(0);
                Judge judge = note.OnHitCheck(keyPressTimes.get(keyCode));
                if(note instanceof Single singleNote) {
                    if(judge != Judge.NONE) {
                        Judgement.judge(judge);
                        deltaTime.put(keyCode, singleNote.getHitTime() - keyPressTimes.get(keyCode));
                        gamePlay.getChildren().remove(singleNote);
                        gamePlay.getBornNotes().get(trackIndex).removeFrontNote();
                        return judge;
                    }
                }else if(note instanceof Hold holdNote) {
                    if(judge != Judge.NONE && holdNote.isStartNote()) {
                        Judgement.judge(judge);
                        deltaTime.put(keyCode, holdNote.getHitTime() - keyPressTimes.get(keyCode));
                        gamePlay.getChildren().remove(holdNote);
                        gamePlay.getBornNotes().get(trackIndex).removeFrontNote();
                        return judge;
                    }
                }
            }
        }
        return Judge.NONE;
    }

    public Judge handleKeyRelease(KeyCode keyCode) {
        currentlyPressedKeys.remove(keyCode);
        int trackIndex = getTrackIndex(keyCode);
        if(trackIndex != -1) {
            keyReleaseTimes.put(keyCode, (int) ((System.nanoTime() - gamePlay.getStartTime()) / 1_000_000));
            List<Note> notes = gamePlay.getBornNotes().get(trackIndex).getNotes();
            if(!notes.isEmpty()) {
                Note note = gamePlay.getBornNotes().get(trackIndex).getNotes().get(0);
                Judge judge = note.OnHitCheck(keyReleaseTimes.get(keyCode));
                if(note instanceof Hold holdNote && holdNote.isEndNote()) {
                    if(judge != Judge.NONE) {
                        Judgement.judge(judge);
                        deltaTime.put(keyCode, holdNote.getHitTime() - keyReleaseTimes.get(keyCode));
                        gamePlay.getChildren().remove(holdNote);
                        gamePlay.getBornNotes().get(trackIndex).removeFrontNote();
                        return judge;
                    }else {
                        holdNote.miss();
                        gamePlay.getChildren().remove(holdNote);
                        gamePlay.getBornNotes().get(trackIndex).removeFrontNote();
                        return Judge.MISS;
                    }
                }
            }else if(!gamePlay.getBeatMap().getTrack(trackIndex).getNotes().isEmpty()) {
                Note note = gamePlay.getBeatMap().getTrack(trackIndex).getNotes().get(0);
                if(note instanceof Hold holdNote && holdNote.isEndNote()) {
                    holdNote.miss();
                    gamePlay.getBeatMap().getTrack(trackIndex).removeFrontNote();
                    return Judge.MISS;
                }
            }
        }
        return Judge.NONE;
    }

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

    public int getKeyPressTime(KeyCode keyCode) {
        return keyPressTimes.getOrDefault(keyCode, 0);
    }

    public int getDeltaTime(KeyCode keyCode) {
        return deltaTime.getOrDefault(keyCode, 0);
    }

    public int getKeyReleaseTime(KeyCode keyCode) {
        return keyReleaseTimes.getOrDefault(keyCode, 0);
    }
}
