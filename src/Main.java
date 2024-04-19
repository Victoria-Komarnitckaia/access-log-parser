import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        int count = 0;
        while (true) {
            System.out.println("Укажите путь к файлу: ");
            String path = new Scanner(System.in).nextLine();
            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();
            if (!fileExists) {
                System.out.println("Указанный файл не существует");
                continue;
            } else if (isDirectory) {
                System.out.println("Указанный путь является путём к папке, а не к файлу");
                continue;
            } else {
                System.out.println("Путь указан верно");
                count++;
                System.out.println("Это файл номер " + count);
            }

            try {
                FileReader fileReader = new FileReader(path);
                BufferedReader reader =
                        new BufferedReader(fileReader);
                String line;
                int countLines = 0, googleBotCount = 0, yandexBotCount = 0;
                while ((line = reader.readLine()) != null) {
                    int length = line.length();
                    if (length > 1024) {
                        throw new LengthException("В файле встретилась строка длиннее 1024 символов");
                    }
                    countLines++;

//                  Делим строку на составляющие (выделяем User-Agent) из шаблона:
//                  " "Mozilla/5.0 (Windows NT 6.1; MegaIndex.ru/2.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36"
                    String userAgent = "";
                    Pattern patternUserAgent = Pattern.compile("(\"\\s\")(.*?)(\")");
                    Matcher matcherUserAgent = patternUserAgent.matcher(line);
                    if (matcherUserAgent.find())
                        userAgent = matcherUserAgent.group(2);

//                  Выделяем часть, которая находится в первых скобках, например (Windows NT 6.1; MegaIndex.ru/2.0)
                    String firstBrackets = "";
                    Pattern pattern = Pattern.compile("(\\()(.*?)(\\))");
                    Matcher matcher = pattern.matcher(userAgent);
                    if (matcher.find())
                        firstBrackets = matcher.group(2);

//                  Берем второй фрагмент, разделенный по ; без пробелов, например MegaIndex.ru/2.0
                    String fragment = "";
                    String[] parts = firstBrackets.replace(" ", "").split(";");
                    if (parts.length >= 2) {
                        fragment = parts[1];
                    }

//                  Делим выделенный фрагмент по слэшу и берем первую часть, например MegaIndex.ru
                    String[] fragmentParts = fragment.split("/");
                    if (fragmentParts.length >= 2) {
                        fragment = fragmentParts[0];
                    }

//                  Cчитаем количество строк в файле, соответствующих запросам от гугл и яндекс ботов
                    if (fragment.equals("Googlebot")) {
                        googleBotCount++;
                    }
                    if (fragment.equals("YandexBot")) {
                        yandexBotCount++;
                    }
                }
                System.out.printf("Доля запросов Googlebot составляет: %.2f%% \n",
                        (double) googleBotCount / countLines * 100);
                System.out.printf("Доля запросов YandexBot составляет: %.2f%% \n",
                        (double) yandexBotCount / countLines * 100);
                System.out.println("Общее количество строк в файле: " + countLines);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
