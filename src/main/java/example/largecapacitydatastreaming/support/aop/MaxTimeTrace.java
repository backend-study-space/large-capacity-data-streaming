package example.largecapacitydatastreaming.support.aop;

public class MaxTimeTrace {

    private String message;

    private long maxTime = 0;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }
}
