package org.example.spacecompany.console;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.example.spacecompany.util.InputReader;

/**
 * Крошечные статические помощники для всех меню: заголовки, пометки
 * успеха/ошибки, пауза «нажми Enter» и выбор номера из списка.
 */
public final class MenuIO {

    private MenuIO() {
    }

    public static void printHeader(String title) {
        System.out.println();
        System.out.println(Ansi.paint("=== " + title + " ===", Ansi.BOLD + Ansi.CYAN));
    }

    public static void ok(String message) {
        System.out.println(Ansi.paint("[OK] ", Ansi.GREEN) + message);
    }

    public static void error(String message) {
        System.out.println(Ansi.paint("[ОШИБКА] ", Ansi.BOLD + Ansi.RED) + message);
    }

    public static void info(String message) {
        System.out.println(message);
    }

    public static void pause(InputReader input) {
        System.out.print("Нажмите Enter, чтобы продолжить...");
        input.readLine("");
    }

    /** Первые 8 символов UUID — короткий id для меню. */
    public static String shortId(UUID id) {
        return id.toString().substring(0, 8);
    }

    /**
     * Печатает элементы с номерами от 1 и даёт выбрать один.
     * Пример generic-метода: работает для ракет, людей, миссий — для чего угодно.
     *
     * @return выбранный элемент или null, если выбрали 0 (отмена)
     */
    public static <T> T choose(List<T> items, String itemName,
                               InputReader input, Function<T, String> describe) {
        if (items.isEmpty()) {
            System.out.println("Список пуст — выбирать нечего.");
            return null;
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.println(Ansi.paint((i + 1) + ". ", Ansi.CYAN) + describe.apply(items.get(i)));
        }
        System.out.println(Ansi.dim("0. Отмена"));
        int choice = input.readInt("Выберите " + itemName + " (0-" + items.size() + "): ", 0, items.size());
        if (choice == 0) {
            return null;
        }
        return items.get(choice - 1);
    }
}
