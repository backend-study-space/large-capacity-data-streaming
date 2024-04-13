package example.largecapacitydatastreaming.support.aop;

import java.util.*;

public class LogId {
    private final String id;
    private int level;
    private final MaxTimeTrace maxTimeTrace;
    private long totalDuration;
    private String beforeMessage;

    public LogId() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.level = 0;
        this.maxTimeTrace = new MaxTimeTrace();
    }

    private LogId callExistLogId(int levelControl) {
        this.level = level + levelControl;

        return this;
    }

    public LogId createNextLevel(int levelControl) {
        return callExistLogId(levelControl);
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public MaxTimeTrace getMaxTime() {
        return maxTimeTrace;
    }

    public void updateMaxTime(long time, String message) {
        if (maxTimeTrace.getMaxTime() < time) {
            maxTimeTrace.setMaxTime(time);
            maxTimeTrace.setMessage(message);
        }
    }

    public void updateTotalDuration(long time) {
        totalDuration += time;
    }

    public double getTotalDuration() {
        return totalDuration / 1000.0;
    }

    public String getBeforeMessage() {
        return beforeMessage;
    }

    public void setBeforeMessage(String beforeMessage) {
        this.beforeMessage = beforeMessage;
    }
}
