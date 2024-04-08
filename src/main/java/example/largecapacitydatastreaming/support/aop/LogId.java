package example.largecapacitydatastreaming.support.aop;

import java.util.UUID;

public class LogId {
    private final String id;
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
}
