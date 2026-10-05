// ============================================================
// QUEST 00 · РЕКРУТ · Строки и первый запуск
// Уровни ROADMAP: LVL 3–5
//
// Как запустить:
//   javac -encoding UTF-8 quests/Quest00_Recruit.java
//   java -cp quests Quest00_Recruit
//
// Правила: методы с пометкой TODO бросают исключение —
// программа компилируется и работает СРАЗУ, но показывает ⬜.
// Твоя задача: заменить каждый throw на настоящий код,
// чтобы все строки стали ✅. Максимум: 70 XP.
// ============================================================

public class Quest00_Recruit {

    static int xp = 0;

    static void check(boolean condition, String name, int reward) {
        if (condition) {
            xp += reward;
            System.out.println("✅ " + name + " (+" + reward + " XP)");
        } else {
            System.out.println("❌ " + name + " — неверный результат, думай ещё");
        }
    }

    static void todo(String name) {
        System.out.println("⬜ " + name + " — ещё не сделано (найди TODO в коде)");
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 00 · РЕКРУТ ===");

        try {
            check(formatFuel("Falcon 9", 200, 200).equals("Falcon 9: 200/200"),
                    "LVL-3 · formatFuel", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-3 · formatFuel");
        }

        try {
            check(shortId("a3ca3e25-ffff-0000-1111-222233334444").equals("a3ca3e25"),
                    "LVL-4 · shortId", 10);
        } catch (UnsupportedOperationException e) {
            todo("LVL-4 · shortId");
        }

        try {
            String log = buildLog(new String[]{"Ignition...", "Liftoff!", "Orbit OK"});
            check(log.equals("Ignition...\nLiftoff!\nOrbit OK\n"), "LVL-5 · buildLog", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-5 · buildLog");
        }

        try {
            check(greet("   Анна  ").equals("Привет, Анна!"), "LVL-5 · greet", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-5 · greet");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 70");
        if (xp == 70) {
            System.out.println("Так держать, рекрут! Доложи о готовности в ROADMAP → RANK 1.");
        }
    }

    // LVL-3 · Верни строку вида "Falcon 9: 200/200".
    // Подсказка: "строка с дырками".formatted(...)
    static String formatFuel(String name, int fuel, int max) {
        // TODO LVL-3: ✍️ напиши return одной строкой через .formatted(...)
        throw new UnsupportedOperationException("LVL-3 not implemented");
    }

    // LVL-4 · Верни первые 8 символов id (короткий id для меню).
    // Подсказка: String.substring(0, 8). Подумай: что будет, если id короче?
    static String shortId(String uuid) {
        // TODO LVL-4: ✍️ допиши метод
        throw new UnsupportedOperationException("LVL-4 not implemented");
    }

    // LVL-5 · Собери строки в один лог: каждая строка + '\n' в конце.
    // Требование: использовать StringBuilder (а не + в цикле!). Почему — узнаешь на LVL-36.
    static String buildLog(String[] lines) {
        // TODO LVL-5: ✍️ создай StringBuilder, допиши цикл, верни .toString()
        throw new UnsupportedOperationException("LVL-5 not implemented");
    }

    // LVL-5 · Убери пробелы по краям и верни "Привет, <имя>!".
    // Подсказка: strip() — в отличие от trim() понимает и unicode-пробелы.
    static String greet(String rawName) {
        // TODO LVL-5: ✍️ допиши метод
        throw new UnsupportedOperationException("LVL-5 not implemented");
    }
}
