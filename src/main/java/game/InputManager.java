package game;

import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputManager {
    private GamePlay gamePlay;
    private Map<KeyCode, Integer> keyPressTimes = new HashMap<>();
    private Map<KeyCode, Integer> deltaTime = new HashMap<>();
    private Map<KeyCode, Integer> keyReleaseTimes = new HashMap<>();

    public InputManager(GamePlay gamePlay) {
        this.gamePlay = gamePlay;
    }

    public void handleKeyPress(KeyCode keyCode) {
        int trackIndex = getTrackIndex(keyCode);
        if(trackIndex != -1) {
            keyPressTimes.put(keyCode, (int)((System.nanoTime() - gamePlay.getStartTime()) / 1_000_000));
            List<Note> notes = gamePlay.getBornedNotes().get(trackIndex).getNotes();
            if(!notes.isEmpty()){
                Note note = gamePlay.getBornedNotes().get(trackIndex).getNotes().get(0);

                if(note instanceof Single singleNote){
                    if(singleNote.OnHitCheck(keyPressTimes.get(keyCode)) != Judge.NONE){
                        deltaTime.put(keyCode, singleNote.getHitTime() - keyPressTimes.get(keyCode));
                        gamePlay.getChildren().remove(singleNote);
                        gamePlay.getBornedNotes().get(trackIndex).removeNote();
                    }
                } else if(note instanceof Hold holdNote) {
                    if(holdNote.OnHitCheck(keyPressTimes.get(keyCode)) != Judge.NONE){
                        deltaTime.put(keyCode, holdNote.getHitTime() - keyPressTimes.get(keyCode));
                        gamePlay.getChildren().remove(holdNote);
                        gamePlay.getBornedNotes().get(trackIndex).removeNote();
                    }
                }
            }
        }
    }

    private int getTrackIndex(KeyCode keyCode) {
        switch (keyCode) {
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
        return keyPressTimes.get(keyCode);
    }

    public int getDeltaTime(KeyCode keyCode) {
        return deltaTime.getOrDefault(keyCode, 0);
    }
}
