package example.largecapacitydatastreaming.support.aop;

import java.util.*;

public class LogId {
    private final String id;
    private final Map<String, Long> timeMap = new HashMap<>();
    private int level;

    public LogId() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.level = 0;
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

    public Map<String, Long> getTimeMap() {
        return timeMap;
    }

    public void addSpendTimes(String message, Long time) {
        timeMap.put(message, time);
    }

    public void updateSpendTimes(String message, Long time) {
        timeMap.put(message, time - timeMap.get(message));
    }
}
