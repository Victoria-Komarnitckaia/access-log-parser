import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Интрефейс для поиска элементов, подходящих по шаблону
 */
public interface Matchable {

    //  метод возвращает все значения, подходящие под шаблон
    default String matchValues(String line, String pattern) {
        Pattern patternResult = Pattern.compile(pattern);
        Matcher matcher = patternResult.matcher(line);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    //  метод возвращает значения из второй группы в шаблоне
    default String matchForSecondGroupValues(String line, String pattern) {
        Pattern patternResult = Pattern.compile(pattern);
        Matcher matcher = patternResult.matcher(line);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }
}
