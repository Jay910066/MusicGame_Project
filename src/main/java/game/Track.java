package game;

import java.util.ArrayList;

/**
 * 譜面
 */
public class Track {
    private final ArrayList<Note> notes;

    public Track() {
        notes = new ArrayList<>();
    }

    /**
     * 取得音符
     * @return 音符
     */
    public ArrayList<Note> getNotes() {
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
        notes.remove(0);
    }
}
