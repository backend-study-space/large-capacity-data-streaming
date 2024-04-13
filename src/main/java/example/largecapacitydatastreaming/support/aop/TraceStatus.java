package example.largecapacitydatastreaming.support.aop;


public class TraceStatus {
    private LogId logId;
    private String message;
    private Long startTime;
    private long durTime;

    public LogId getLogId() {
        return logId;
    }

    public void setLogId(LogId logId) {
        this.logId = logId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public long getDurTime() {
        return durTime;
    }

    public void setDurTime(long durTime) {
        this.durTime = durTime;
    }

    public TraceStatus(LogId logId, String message, Long startTime, long durTime) {
        this.logId = logId;
        this.message = message;
        this.startTime = startTime;
        this.durTime = durTime;
    }
}
