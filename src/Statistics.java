import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private final HashSet<String> existingPages;
    private final HashMap<String, Integer> operationSystemFrequencyOfOccurrence;
    private final HashSet<String> nonExistingPages;
    private final HashMap<String, Integer> browserFrequencyOfOccurrence;
    private int numberOfBrowsers;
    private int numberOfFailedRequests;
    private final Set<String> ipAddresses;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
        this.existingPages = new HashSet<>();
        this.operationSystemFrequencyOfOccurrence = new HashMap<>();
        this.nonExistingPages = new HashSet<>();
        this.browserFrequencyOfOccurrence = new HashMap<>();
        this.numberOfBrowsers = 0;
        this.numberOfFailedRequests = 0;
        this.ipAddresses=new HashSet<>();
    }

    public void addEntry(List<LogEntry> logEntry) {
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
                setFrequencyOfOccurrence(operationSystemFrequencyOfOccurrence, operationSystemType);
            }
            if (entry.referer != null && entry.responseCode == 404) {
                nonExistingPages.add(entry.referer);
            }
            if (entry.userAgent != null && entry.userAgent.browser != null) {
                String browser = entry.userAgent.browser;
                setFrequencyOfOccurrence(browserFrequencyOfOccurrence, browser);
            }
            if (entry.userAgent != null && !entry.userAgent.isBot) {
                numberOfBrowsers++;
            }
            if (entry.responseCode >= 400 && entry.responseCode <= 599) {
                numberOfFailedRequests++;
            }
            if (entry.ipAddr != null) {
                ipAddresses.add(entry.ipAddr);
            }
        }
    }

    public long getAverageTrafficPerUser() {
        return numberOfBrowsers / ipAddresses.size();
    }

    public long getAverageOfFailedRequestsPerHour() {
        long hoursDuration = Duration.between(minTime, maxTime).toHours();
        return numberOfFailedRequests / hoursDuration;
    }

    public long getAverageVisitsPerHour() {
        long hoursDuration = Duration.between(minTime, maxTime).toHours();
        return numberOfBrowsers / hoursDuration;
    }

    public HashMap<String, Double> getOperationSystemStatistic() {
        return getStatistic(operationSystemFrequencyOfOccurrence);
    }

    public HashMap<String, Double> getBrowserStatistic() {
        return getStatistic(browserFrequencyOfOccurrence);
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

    public HashSet<String> getNonExistingPages() {
        return nonExistingPages;
    }

    public HashMap<String, Integer> getBrowserFrequencyOfOccurrence() {
        return browserFrequencyOfOccurrence;
    }

    public int getNumberOfBrowsers() {
        return numberOfBrowsers;
    }

    public int getNumberOfFailedRequests() {
        return numberOfFailedRequests;
    }

    public Set<String> getIpAddresses() {
        return ipAddresses;
    }

    private void setFrequencyOfOccurrence(
            HashMap<String, Integer> frequencyOfOccurrenceValue,
            String value
    ) {
        // если уже есть ключ, то прибавляем единицу
        if (frequencyOfOccurrenceValue.containsKey(value)) {
            frequencyOfOccurrenceValue.put(
                    value,
                    frequencyOfOccurrenceValue.get(value) + 1
            );
            // если нет, то кладем ключ и присваиваем значение 1
        } else {
            frequencyOfOccurrenceValue.put(value, 1);
        }
    }

    private HashMap<String, Double> getStatistic(HashMap<String, Integer> frequencyOfOccurrenceValue) {
        int totalValue = 0;
        HashMap<String, Double> statistic = new HashMap<>();
        for (Integer frequency : frequencyOfOccurrenceValue.values()) {
            totalValue += frequency;
        }
        for (Map.Entry<String, Integer> entry : frequencyOfOccurrenceValue.entrySet()) {
            Double value = (double) entry.getValue() / totalValue;
            statistic.put(entry.getKey(), value);
        }
        return statistic;
    }
}
