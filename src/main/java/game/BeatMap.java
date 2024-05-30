package game;

import java.util.ArrayList;

/**
 * 譜面
 */
public class BeatMap {
    private final ArrayList<Track> tracks;

    public BeatMap() {
        tracks = new ArrayList<>(4);
        for(int i = 0; i < 4; i++) {
            tracks.add(new Track());
        }
    }

    /**
     * 取得音軌
     * @return 音軌
     */
    public Track getTrack(int index) {
        return tracks.get(index);
    }
}
