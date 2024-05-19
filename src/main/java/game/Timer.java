package game;

import java.time.Instant;

public class Timer {
    private Instant startTime;
    private Instant currentTime;
    private Instant pauseTime;
    private long totalPauseTime;

    public Timer() {
        startTime = Instant.now();
        totalPauseTime = 0;
    }

    public int getCurrentTime() {
        currentTime = Instant.now();
        return (int) (currentTime.toEpochMilli() - startTime.toEpochMilli() - totalPauseTime);
    }

    public void pause() {
        pauseTime = Instant.now();
    }

    public void resume() {
        totalPauseTime += Instant.now().toEpochMilli() - pauseTime.toEpochMilli();
    }
}
