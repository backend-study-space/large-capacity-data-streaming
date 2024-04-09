package example.largecapacitydatastreaming.support.aop;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.Map;

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
        long startTime = System.currentTimeMillis();

        logId.addSpendTimes(message, startTime);

        return new TraceStatus(logId, startTime);
    }

    public void end(String message, TraceStatus traceStatus) {
        LogId logId = traceIdHolder.get();

        long endTime = System.currentTimeMillis();

        logId.updateSpendTimes(message, endTime);

        if (logId.getLevel() == 0 ) {
            traceIdHolder.remove();
            Map<String, Long> timeMap = logId.getTimeMap();

            Map.Entry<String, Long> maxEntry = timeMap.entrySet()
                    .stream()
                    .max(Comparator.comparingLong(Map.Entry::getValue))
                    .orElse(null);

            log.info("병목 발생 지점 :", maxEntry.getKey(), maxEntry.getValue());
        } else {
            traceIdHolder.set(logId.createNextLevel(-1));
        }

        log.info("[{}], [{}ms]", traceStatus.logId().getId(), traceStatus.startTime() - endTime);
    }
}
