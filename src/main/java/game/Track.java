package game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 * 音符軌道
 */
public class Track {
    private final Queue<Note> notes;

    public Track() {
        notes = new ArrayDeque<>();
    }

    /**
     * 取得音符
     * @return 音符
     */
    public Queue<Note> getNotes() {
        return notes;
    }

    /**
     * 新增音符
     * @param note 音符
     */
    public void addNote(Note note) {
        notes.add(note);
    }

    /**
     * 移除最前面的音符
     */
    public void removeFrontNote() {
        notes.remove();
    }
}
