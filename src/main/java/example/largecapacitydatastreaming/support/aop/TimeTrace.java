package example.largecapacitydatastreaming.support.aop;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

@Component
public class TimeTrace {

    private final ThreadLocal<LogId> traceIdHolder = new ThreadLocal<>();

    private final Logger log = LoggerFactory.getLogger(TimeTrace.class);

    public TraceStatus start(String message) {
        LogId logId = traceIdHolder.get();

        if (logId == null) {
            logId = new LogId();
            traceIdHolder.set(logId);
        } else {
            traceIdHolder.set(logId.createNextLevel(1));
        }

        log.info("[{}], [{}]", logId.getId(), message);

        return new TraceStatus(logId, System.currentTimeMillis());
    }

    public void end(TraceStatus traceStatus) {
        LogId logId = traceIdHolder.get();

        if (logId.getLevel() == 0 ) {
            traceIdHolder.remove();
        } else {
            traceIdHolder.set(logId.createNextLevel(-1));
        }

        log.info("[{}], [{}ms]", traceStatus.logId().getId(), traceStatus.startTime() - System.currentTimeMillis());
    }
}
