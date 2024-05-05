import java.util.ArrayList;
import java.util.List;

public class UserAgent implements Matchable {
    final OperationSystemType operationSystemType;
    final String browser;
    final boolean isBot;

    public UserAgent(String userAgent) {
//      пример входящей строки Mozilla/5.0 (Windows NT 6.1; MegaIndex.ru/2.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36
        this.operationSystemType = setOperationSystemType(userAgent);
        this.browser = setBrowser(userAgent);
        this.isBot = isBot(userAgent);
    }

    public OperationSystemType getOperationSystemType() {
        return operationSystemType;
    }

    public String getBrowser() {
        return browser;
    }

    public boolean isBot() {
        return isBot;
    }

    private OperationSystemType setOperationSystemType(String userAgent) {
        //      убираем пробелы и ищем ОС (в задании указано строго 3 типа операционной системы Windows, macOS или Linux - передала их в enum)
        String newUserAgent = userAgent.replace(" ", "").toUpperCase();
        List<String> oSTypeList = new ArrayList<>();
        for (int i = 0; i < OperationSystemType.values().length; i++) {
            String oSTypeValue = matchValues(newUserAgent, OperationSystemType.values()[i].toString());
            if (oSTypeValue != null) {
                oSTypeList.add(oSTypeValue);
            }
        }
        if (oSTypeList.size() > 0) {
            return OperationSystemType.valueOf(oSTypeList.get(0));
        } else {
            return null;
        }
    }

    private String setBrowser(String userAgent) {
        //      ищем браузеры
        if (userAgent.contains("Gecko")) {
//          Gecko) Chrome/35.0.1916.114 во вторую часть попадет Chrome/35.0.1916.114 без пробела
            String[] parts = userAgent.split("Gecko\\S+\\s");
            if (parts.length >= 2) {
                if (parts[1].contains("Browser")) {
//                  XiaoMi/MiuiBrowser/12.4.1-g - результат MiuiBrowser
                    return matchValues(parts[1], "\\w*Browser\\w*");
                } else if (!parts[1].contains("Chrome") && !parts[1].contains("Version")) {
//                  первый элемент массива, пример Firefox/35.0.1916.114
                    String res = matchValues(parts[1], "\\S+/");
                    if (res != null) {
                        res = res.replace("/", "");
                        return res;
                    } else {
                        return null;
                    }
                } else if (parts[1].contains("Version") && parts[1].contains("Safari")) {
                    return "Safari";
                } else if (parts[1].contains("Chrome") && parts[1].contains("Safari") && parts[1].contains("OPR/")) {
                    return "Opera";
                } else if (parts[1].contains("Chrome") && parts[1].contains("Safari") && parts[1].contains("Edg")) {
                    return "Edge";
                } else if (parts[1].contains("Chrome") && parts[1].contains("Safari")) {
                    return "Chrome";
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private boolean isBot(String userAgent) {
        return userAgent.toUpperCase().contains("bot".toUpperCase());
    }
}


