package game;

import java.util.ArrayList;

public class Track {
    private ArrayList<Note> notes;

    public Track() {

    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void addNote(Note note) {
        notes.add(note);
    }
}
