import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LogEntry implements Matchable {
    final String ipAddr;
    final LocalDateTime time;
    final HttpMethod method;
    final String path;
    final int responseCode;
    final int responseSize;
    final String referer;
    final UserAgent userAgent;

    public LogEntry(String logLine) {
//      выбираем ip адрес по маске 10.142.178.73
        this.ipAddr = matchValues(logLine, "([0-9]{1,3}[\\.]){3}[0-9]{1,3}");

//      выбираем дату и время запроса по маске [25/Sep/2022:06:25:04 +0300]
//      скобки объединяем в первую группу, само значение во вторую
        String timeMatchedValue = matchForSecondGroupValues(logLine, "(\\[)(.*?)]");
        if (timeMatchedValue != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            this.time = LocalDateTime.parse(timeMatchedValue, formatter);
        } else {
            this.time = null;
        }

//      выбираем метод запроса
        List<HttpMethod> httpMethodList = new ArrayList<>();
        for (int i = 0; i < HttpMethod.values().length; i++) {
            String httpMethodValue = matchValues(logLine, HttpMethod.values()[i].toString());
            if (httpMethodValue != null) {
                httpMethodList.add(HttpMethod.values()[i]);
            }
        }
        if (httpMethodList.size() > 0) {
            this.method = httpMethodList.get(0);
        } else {
            this.method = null;
        }

//      выбираем путь, по которому сделан запрос по маске  /data.php?rss=1&json=1&lg=2 HTTP/1.0"
//      пробел до слэша объединяем в первую группу, сам урл вторую
        this.path = matchForSecondGroupValues(logLine, "(\\s)(/\\S*)(\\s)");

//      выбираем код HTTP-ответа по маске  " 200  кавычки и пробел объединяем в первую группу, цифры во вторую
        String responseCodeResult = matchForSecondGroupValues(logLine, "(\"\\s)([0-9]{3})(\\s)");
        if (responseCodeResult != null) {
            this.responseCode = Integer.parseInt(responseCodeResult);
        } else {
            this.responseCode = 0;
        }

//      выбираем размер отданных данных в байтах по маске 61096 "  пробел до цифр объединяем в первую группу, цифры во вторую
        String responseSizeResult = matchForSecondGroupValues(logLine, "(\\s+)([0-9]+)(\\s+\")");
        if (responseSizeResult != null) {
            this.responseSize = Integer.parseInt(responseSizeResult);
        } else {
            this.responseSize = 0;
        }

//      выбираем путь к странице, с которой перешли на текущую страницу по маске "https://nova-news.ru/search/?rss=1&lg=1" "
//      пробел и кавычки до урла объединяем в первую группу, сам урл вторую
        String refererPattern = matchForSecondGroupValues(logLine, "(\\s\")(\\S*)(\"\\s\")");
        if (!refererPattern.equals("-")) {
            this.referer = refererPattern;
        } else {
            this.referer = null;
        }

//      выделяем User-Agent по маске
//      " "Mozilla/5.0 (Windows NT 6.1; MegaIndex.ru/2.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36"
//      пробел и кавычки объединяем в первую группу, значение юзер-агента во вторую
        String userAgentResultValue = matchForSecondGroupValues(logLine, "(\"\\s\")(.*?)(\")");
        if (userAgentResultValue != null && !userAgentResultValue.equals("-")) {
            this.userAgent = new UserAgent(userAgentResultValue);
        } else {
            this.userAgent = null;
        }
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }
}

