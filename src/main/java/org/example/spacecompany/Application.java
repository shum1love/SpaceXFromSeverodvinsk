package org.example.spacecompany;

import org.example.spacecompany.console.Ansi;
import org.example.spacecompany.console.ConsoleMenu;
import org.example.spacecompany.util.InputReader;

/**
 * Консольная точка входа: собирает {@link GameSession} (общее ядро),
 * спрашивает название компании и запускает текстовое меню.
 *
 * <p>Тот же {@link GameSession} использует и JavaFX-интерфейс —
 * «одно ядро, два лица». Класс тонкий: решения живут в сервисах и меню.
 */
public class Application {

    private final InputReader input = new InputReader();

    public void run() {
        printBanner();

        String name = input.readLine("Название вашей космической компании [Северодвинские Звёзды]: ").strip();
        GameSession session = new GameSession(name.isEmpty() ? null : name);

        System.out.println("Город подарил вам Falcon 9 «Северодвинск-1» и двух специалистов.");
        System.out.println("Подсказка: заправьте ракету, создайте миссию, назначьте экипаж — и в полёт!");
        System.out.println("Компания: " + session.company());

        ConsoleMenu menu = new ConsoleMenu(session.company(), session.finance(), session.rockets(),
                session.employees(), session.missions(), session.statistics(), session.saveLoad(), input);
        try {
            menu.run();
        } finally {
            // Настоящее применение finally: фоновый пул потоков и консольный
            // ввод освобождаются, даже если меню упадёт с ошибкой.
            session.shutdown();
            input.close();
        }
    }

    /** Приветственный баннер. Цвета — через Ansi (сами отключатся в пайпе). */
    private static void printBanner() {
        String line = Ansi.paint("==========================================", Ansi.CYAN);
        System.out.println(line);
        System.out.println(Ansi.paint("     🚀 СИМУЛЯТОР КОСМИЧЕСКОЙ КОМПАНИИ", Ansi.BOLD));
        System.out.println(Ansi.paint("        Из Северодвинска — к звёздам!", Ansi.YELLOW));
        System.out.println(line);
    }
}
