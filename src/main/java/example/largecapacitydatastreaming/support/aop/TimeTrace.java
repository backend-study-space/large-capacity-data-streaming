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

        return new TraceStatus(logId, message, System.currentTimeMillis());
    }

    public void end(TraceStatus traceStatus) {
        LogId logId = traceIdHolder.get();

        long endTime = System.currentTimeMillis();
        logId.updateMaxTime(endTime - traceStatus.startTime(), traceStatus.message());
        logId.updateTotalDuration(endTime - traceStatus.startTime());

        log.info("[{}], [{}], [{}ms]", traceStatus.logId().getId(), traceStatus.message(), endTime - traceStatus.startTime());

        if (logId.getLevel() == 0 ) {
            traceIdHolder.remove();

            log.info("[{}] 최대 병목 시간 : [{}], [{}ms]", logId.getId(), logId.getMaxTime().getMessage(), logId.getMaxTime().getMaxTime());
            log.info("[{}] 총 소요 시간 : [{}ms]", logId.getId(), logId.getTotalDuration());
        } else {
            traceIdHolder.set(logId.createNextLevel(-1));
        }
    }
}
