package game;

import java.util.ArrayList;

public class Track {
    private ArrayList<Note> notes;

    public Track() {
        notes = new ArrayList<Note>();
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public void removeNote() {
        notes.remove(0);
    }
}
