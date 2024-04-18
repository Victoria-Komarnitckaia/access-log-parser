import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

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
                int countLines = 0;
                int maxLength = 0;
                int minLength = Integer.MAX_VALUE;
                while ((line = reader.readLine()) != null) {
                    int length = line.length();
                    if (length > 1024) {
                        throw new LengthException("В файле встретилась строка длиннее 1024 символов");
                    }
                    if (maxLength < length) {
                        maxLength = length;
                    }
                    if (minLength > length) {
                        minLength = length;
                    }
                    countLines++;
                }
                System.out.println("Общее количество строк в файле: " + countLines);
                System.out.println("Длина самой длинной строки в файле: " + maxLength);
                System.out.println("Длина самой короткой строки в файле: " + minLength);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
