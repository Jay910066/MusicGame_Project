package game;

public class Note {
    private int track;
    private int time;
    private int type;
    private int endTime;

    Note(int track, int time, int type) {
        this.track = track;
        this.time = time;
        this.type = type;
    }

    Note(int track, int time, int type, int endTime) {
        this.track = track;
        this.time = time;
        this.type = type;
        this.endTime = endTime;
    }
}
