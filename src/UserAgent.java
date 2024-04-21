import java.util.ArrayList;
import java.util.List;

public class UserAgent implements Matchable {
    final OperationSystemType operationSystemType;
    final String browser;

    public UserAgent(String userAgent) {
//      пример входящей строки Mozilla/5.0 (Windows NT 6.1; MegaIndex.ru/2.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36

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
            this.operationSystemType = OperationSystemType.valueOf(oSTypeList.get(0));
        } else {
            this.operationSystemType = null;
        }

//      ищем браузеры
        if (userAgent.contains("Gecko")) {
//          Gecko) Chrome/35.0.1916.114 во вторую часть попадет Chrome/35.0.1916.114 без пробела
            String[] parts = userAgent.split("Gecko\\S+\\s");
            if (parts.length >= 2) {
                if (parts[1].contains("Browser")) {
//                  XiaoMi/MiuiBrowser/12.4.1-g - результат MiuiBrowser
                    browser = matchValues(parts[1], "\\w*Browser\\w*");
                } else if (!parts[1].contains("Chrome") && !parts[1].contains("Version")) {
//                  первый элемент массива, пример Firefox/35.0.1916.114
                    String res = matchValues(parts[1], "\\S+/");
                    if (res != null) {
                        res = res.replace("/", "");
                        browser = res;
                    } else {
                        browser = null;
                    }
                } else if (parts[1].contains("Version") && parts[1].contains("Safari")) {
                    browser = "Safari";
                } else if (parts[1].contains("Chrome") && parts[1].contains("Safari") && parts[1].contains("OPR/")) {
                    browser = "Opera";
                } else if (parts[1].contains("Chrome") && parts[1].contains("Safari") && parts[1].contains("Edg")) {
                    browser = "Edge";
                } else if (parts[1].contains("Chrome") && parts[1].contains("Safari")) {
                    browser = "Chrome";
                } else {
                    browser = null;
                }
            } else {
                browser = null;
            }
        } else {
            browser = null;
        }
    }

    public OperationSystemType getOperationSystemType() {
        return operationSystemType;
    }

    public String getBrowser() {
        return browser;
    }
}


