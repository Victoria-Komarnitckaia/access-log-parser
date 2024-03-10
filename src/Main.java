import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите первое число: ");
        int firstNumber = new Scanner(System.in).nextInt();
        System.out.println("Введите второе число: ");
        int secondNumber = new Scanner(System.in).nextInt();
        System.out.println("Сумма введенных чисел равна: " + (firstNumber + secondNumber));
        System.out.println("Разность введенных чисел равна: " + (firstNumber - secondNumber));
        System.out.println("Произведение введенных чисел равно: " + (firstNumber * secondNumber));
        System.out.println("Частное введенных чисел равно: " + ((double) firstNumber / secondNumber));
    }
}
