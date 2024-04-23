import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private final HashSet<String> existingPages;
    private final HashMap<String, Integer> operationSystemFrequencyOfOccurrence;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
        this.existingPages = new HashSet<>();
        this.operationSystemFrequencyOfOccurrence = new HashMap<>();
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
            if (entry.referer != null && entry.responseCode == 200) {
                existingPages.add(entry.referer);
            }
            if (entry.userAgent != null && entry.userAgent.operationSystemType != null) {
                String operationSystemType = entry.userAgent.operationSystemType.toString();
                // если уже есть ключ, то прибавляем единицу
                if (operationSystemFrequencyOfOccurrence.containsKey(operationSystemType)) {
                    operationSystemFrequencyOfOccurrence.put(
                            operationSystemType,
                            operationSystemFrequencyOfOccurrence.get(operationSystemType) + 1
                    );
                    // если нет, то кладем ключ и присваиваем значение 1
                } else {
                    operationSystemFrequencyOfOccurrence.put(operationSystemType, 1);
                }
            }
        }
    }

    public HashMap<String, Double> operationSystemStatistic() {
        int totalOperationSystem = 0;
        HashMap<String, Double> operationSystemStatistic = new HashMap<>();
        for (Integer frequency : operationSystemFrequencyOfOccurrence.values()) {
            totalOperationSystem += frequency;
        }
        for (Map.Entry<String, Integer> entry : operationSystemFrequencyOfOccurrence.entrySet()) {
            Double value = (double) entry.getValue() / totalOperationSystem;
            operationSystemStatistic.put(entry.getKey(), value);
        }
        return operationSystemStatistic;
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

    public HashSet<String> getExistingPages() {
        return existingPages;
    }

    public HashMap<String, Integer> getOperationSystemFrequencyOfOccurrence() {
        return operationSystemFrequencyOfOccurrence;
    }
}
