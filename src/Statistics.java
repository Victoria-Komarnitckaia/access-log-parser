import java.time.Duration;
import java.time.LocalDateTime;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
    }

    public void addEntry(LogEntry[] logEntry) {
        for (LogEntry entry : logEntry) {
            totalTraffic += entry.responseSize;
            if (entry.time != null && entry.time.isBefore(minTime)) {
                minTime = entry.time;
            }
            if (entry.time != null && entry.time.isAfter(maxTime)) {
                maxTime = entry.time;
            }
        }
    }

    public long getTrafficRate() {
        long hoursDuration = Duration.between(minTime, maxTime).toHours();
        return totalTraffic / hoursDuration;
    }

    public long getTotalTraffic() {
        return totalTraffic;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }
}
