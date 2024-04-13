package example.largecapacitydatastreaming.support.aop;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

import java.util.*;

@Component
public class TimeTrace {

    private final ThreadLocal<LogId> traceIdHolder = new ThreadLocal<>();

    private final Logger log = LoggerFactory.getLogger(TimeTrace.class);

    Map<String, TraceStatus> map = new HashMap<>();

    public TraceStatus start(String message) {
        LogId logId = traceIdHolder.get();

        if (logId == null) {
            logId = new LogId();
            traceIdHolder.set(logId);
            logId.setBeforeMessage(message);
            log.info("[{}], [{}]", logId.getId(), message);
        } else {
            logId.createNextLevel(logId.getLevel() + 1);
        }

        if (!logId.getBeforeMessage().equals(message)) {
            log.info("[{}], [{}]", logId.getId(), message);
            logId.setBeforeMessage(message);
        }

        return new TraceStatus(logId, message, System.currentTimeMillis(), 0);
    }

    public void end(TraceStatus traceStatus) {
        LogId logId = traceIdHolder.get();

        long endTime = System.currentTimeMillis();

        updateTime(traceStatus, endTime, logId);

        TraceStatus existingStatus = map.get(traceStatus.getMessage());

        if (existingStatus != null) {
            existingStatus.setDurTime(existingStatus.getDurTime() + (endTime - traceStatus.getStartTime()));
        } else {
            existingStatus = traceStatus;
        }
        map.put(traceStatus.getMessage(), existingStatus);

        if (logId.getLevel() == 0 ) {
            traceIdHolder.remove();

            loggingEndedMethod();

            log.info("[{}] 최대 병목 시간 : [{}], [{}s]", logId.getId(), logId.getMaxTime().getMessage(), logId.getMaxTime().getMaxTime());
            log.info("[{}] 총 소요 시간 : [{}s]", logId.getId(), logId.getTotalDuration());
        } else {
            traceIdHolder.set(logId.createNextLevel(-1));
        }
    }

    private void loggingEndedMethod() {
        List<Map.Entry<String, TraceStatus>> entryList = new ArrayList<>(map.entrySet());

        for (int i = entryList.size() - 1; i >= 0; i--) {
            Map.Entry<String, TraceStatus> entry = entryList.get(i);
            TraceStatus value = entry.getValue();
            log.info("[{}], [{}], [{}s]", value.getLogId().getId(), value.getMessage(), value.getDurTime() / 1000.0);
        }
    }

    private static void updateTime(TraceStatus traceStatus, long endTime, LogId logId) {
        traceStatus.setDurTime(endTime - traceStatus.getStartTime());
        logId.updateMaxTime(endTime - traceStatus.getStartTime(), traceStatus.getMessage());
        logId.updateTotalDuration(endTime - traceStatus.getStartTime());
    }
}
