package org.example.spacecompany.util;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Помощник консольного ввода: спроси → прочитай → проверь → повтори.
 *
 * <p>Владеет одним {@link Scanner} на {@code System.in}. Сканнер специально
 * <b>не</b> закрывается после каждого чтения (закрытие убило бы сам
 * {@code System.in}); метод {@link #close()} вызывается один раз при выходе.
 */
public final class InputReader implements AutoCloseable {

    private final Scanner scanner;

    public InputReader() {
        this.scanner = new Scanner(System.in);
    }

    /** Читает сырую строку (может быть пустой, но не null). */
    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /** Читает непустую строку, переспрашивая, пока не введут хоть что-то. */
    public String readNonBlankLine(String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (line != null && !line.isBlank()) {
                return line.strip();
            }
            System.out.println("Введите непустое значение.");
        }
    }

    /**
     * Читает целое число в диапазоне [min, max]. Ошибка ввода — сообщение
     * и повтор (обычный цикл + try/catch, наружу исключения не летят).
     */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                int value = Integer.parseInt(line.strip());
                if (value < min || value > max) {
                    System.out.printf("Введите число от %d до %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Это не число, попробуйте ещё раз.");
            }
        }
    }

    /** Читает неотрицательную сумму денег, переспрашивая при ошибке. */
    public BigDecimal readMoney(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                BigDecimal value = MoneyUtils.parse(line);
                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("Сумма не должна быть отрицательной.");
                    continue;
                }
                return value;
            } catch (NumberFormatException | ArithmeticException e) {
                System.out.println("Не понял сумму, попробуйте ещё (пример: 1500.50).");
            }
        }
    }

    /** Вопрос да/нет. Понимает y/yes/д/да и n/no/н/нет (регистр не важен). */
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " [д/н]: ");
            String line = scanner.nextLine().strip().toLowerCase();
            switch (line) {
                case "y", "yes", "д", "да" -> {
                    return true;
                }
                case "n", "no", "н", "нет" -> {
                    return false;
                }
                default -> System.out.println("Ответьте д (да) или н (нет).");
            }
        }
    }

    @Override
    public void close() {
        scanner.close();
    }
}
