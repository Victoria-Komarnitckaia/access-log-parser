import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Statistics implements Matchable {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private final HashSet<String> existingPages;
    private final HashMap<String, Integer> operationSystemFrequencyOfOccurrence;
    private final HashSet<String> nonExistingPages;
    private final HashMap<String, Integer> browserFrequencyOfOccurrence;
    private int numberOfBrowsers;
    private int numberOfFailedRequests;
    private final List<String> ipAddressesList;
    private final List<LocalDateTime> noBotTimeList;
    private final List<String> refererList;

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
        this.ipAddressesList = new ArrayList<>();
        this.noBotTimeList = new ArrayList<>();
        this.refererList = new ArrayList<>();
    }

    public void addEntry(List<LogEntry> logEntry) {
        for (LogEntry entry : logEntry) {
            totalTraffic += entry.responseSize;
            if (entry.time != null) {
                if (entry.time.isBefore(minTime)) {
                    minTime = entry.time;
                }
                if (entry.time.isAfter(maxTime)) {
                    maxTime = entry.time;
                }
            }
            if (entry.referer != null) {
                refererList.add(entry.referer);
                if (entry.responseCode == 200) {
                    existingPages.add(entry.referer);
                }
                if (entry.responseCode == 404) {
                    nonExistingPages.add(entry.referer);
                }
            }
            if (entry.userAgent != null) {
                if (entry.userAgent.operationSystemType != null) {
                    String operationSystemType = entry.userAgent.operationSystemType.toString();
                    setFrequencyOfOccurrence(operationSystemFrequencyOfOccurrence, operationSystemType);
                }
                if (entry.userAgent.browser != null) {
                    String browser = entry.userAgent.browser;
                    setFrequencyOfOccurrence(browserFrequencyOfOccurrence, browser);
                }
                if (!entry.userAgent.isBot) {
                    numberOfBrowsers++;
                    noBotTimeList.add(entry.time);
                    if (entry.ipAddr != null) {
                        ipAddressesList.add(entry.ipAddr);
                    }
                }
            }
            if (entry.responseCode >= 400 && entry.responseCode <= 599) {
                numberOfFailedRequests++;
            }
        }
    }

    public int getMaxTrafficPerUser() {
        HashMap<String, Integer> trafficPerUser = new HashMap<>();
        ipAddressesList.forEach(ip -> {
            if (trafficPerUser.containsKey(ip)) {
                trafficPerUser.put(ip, trafficPerUser.get(ip) + 1);
            } else {
                trafficPerUser.put(ip, 1);
            }
        });
        return trafficPerUser.values().stream().max(Integer::compare).get();
    }

    public HashSet<String> getRefererDomainList() {
        HashSet<String> refererDomainList = new HashSet<>();
        refererList.forEach(x -> {
            String part;
            if (x.contains("www")) {
                part = x.split("www.")[1];
            } else {
                part = x.split("//")[1];
            }
            //      выбираем домен по маске, пример nova-news.ru
            refererDomainList.add(matchValues(part, "([A-Za-z0-9+_.-]+)"));
        });
        return refererDomainList;
    }

    public HashMap<Integer, Integer> getPeakWebsiteTrafficPerSecond() {
        HashMap<Integer, Integer> peakWebsiteTrafficPerSecond = new HashMap<>();
        noBotTimeList.forEach(s -> {
            int sec = s.getSecond();
            if (peakWebsiteTrafficPerSecond.containsKey(sec)) {
                peakWebsiteTrafficPerSecond.put(sec, peakWebsiteTrafficPerSecond.get(sec) + 1);
            } else {
                peakWebsiteTrafficPerSecond.put(sec, 1);
            }
        });
        return peakWebsiteTrafficPerSecond;
    }

    public long getAverageTrafficPerUser() {
        Set<String> ipAddresses = new HashSet<>(ipAddressesList);
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

    public List<String> getIpAddressesList() {
        return ipAddressesList;
    }

    public List<LocalDateTime> getNoBotTimeList() {
        return noBotTimeList;
    }

    public List<String> getRefererList() {
        return refererList;
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
