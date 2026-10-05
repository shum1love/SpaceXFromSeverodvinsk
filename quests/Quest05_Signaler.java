// ============================================================
// QUEST 05 · СВЯЗИСТ · StringBuilder, деньги, время, файлы
// Уровни ROADMAP: LVL 36–40
//
//   javac -encoding UTF-8 quests/Quest05_Signaler.java
//   java -cp quests Quest05_Signaler
//
// Максимум: 130 XP.
// ============================================================

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Quest05_Signaler {

    static int xp = 0;

    static void check(boolean condition, String name, int reward) {
        if (condition) {
            xp += reward;
            System.out.println("✅ " + name + " (+" + reward + " XP)");
        } else {
            System.out.println("❌ " + name + " — неверный результат");
        }
    }

    static void todo(String name) {
        System.out.println("⬜ " + name + " — ещё не сделано");
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 05 · СВЯЗИСТ ===");

        try {
            // Оба метода должны вернуть одинаковый лог — но собранный по-разному.
            String expected = "A\nB\nC\n";
            check(buildWithPlus(List.of("A", "B", "C")).equals(expected)
                    && buildWithBuilder(List.of("A", "B", "C")).equals(expected),
                    "LVL-36 · + vs StringBuilder: одинаковый результат", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-36 · + vs StringBuilder");
        }

        try {
            // Парсинг денег в копейках: "1_000.50" → 100050, "250" → 25000.
            // Подсказка: убери "_" и ".", потом Integer.parseInt. Точку тоже убрать!
            check(parseCents("1_000.50") == 100050 && parseCents("250") == 25000,
                    "LVL-37 · парсинг денег", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-37 · парсинг денег");
        }

        try {
            // Сколько дней между основанием компании и сегодня? (ChronoUnit.DAYS.between)
            LocalDate founded = LocalDate.of(2026, 10, 4);
            LocalDate today = LocalDate.of(2026, 11, 3);
            check(daysSince(founded, today) == 30, "LVL-38 · java.time: дни между датами", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-38 · java.time: дни между датами");
        }

        try {
            // Экранирование как в сейвах проекта: '|' → "\p", '\n' → "\n", '\' → "\\".
            String tricky = "A|B\nC\\D";
            String escaped = escape(tricky);
            check(escaped.equals("A\\pB\\nC\\\\D") && unescape(escaped).equals(tricky),
                    "LVL-39/40 · escape/unescape туда-обратно", 40);
        } catch (UnsupportedOperationException e) {
            todo("LVL-39/40 · escape/unescape туда-обратно");
        }

        try {
            // Разбей по НЕэкранированному '|': "A\\pB|C" → ["A\\pB", "C"] (2 куска!).
            List<String> parts = splitEscaped("A\\pB|C");
            check(parts.size() == 2 && parts.get(0).equals("A\\pB") && parts.get(1).equals("C"),
                    "LVL-40 · split по неэкранированному разделителю", 30);
        } catch (UnsupportedOperationException e) {
            todo("LVL-40 · split по неэкранированному разделителю");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 130");
    }

    // LVL-36 · Собери лог через + в цикле (так делать НЕЛЬЗЯ в проде, но почувствуй разницу).
    static String buildWithPlus(List<String> lines) {
        // TODO LVL-36: ✍️ String result = ""; цикл: result = result + line + "\n";
        throw new UnsupportedOperationException("LVL-36 not implemented");
    }

    // LVL-36 · Тот же лог через StringBuilder (так — правильно).
    static String buildWithBuilder(List<String> lines) {
        // TODO LVL-36: ✍️ StringBuilder sb = new StringBuilder(); цикл append; return sb.toString();
        throw new UnsupportedOperationException("LVL-36 not implemented");
    }

    // LVL-37 · Деньги строкой → копейки числом. Убери "_" и "." и распарси int.
    // "1_000.50" → убери мусор → "100050" → 100050. А "250" → "250" → 250?? ЛОВУШКА!
    // Подумай: "250" — это 250 рублей = 25000 копеек. Как отличить от "250.00"?
    // Правило: если точки нет — домножь на 100. Реализуй это правило!
    static int parseCents(String money) {
        // TODO LVL-37: ✍️ убери "_", проверь наличие ".", дальше арифметика
        throw new UnsupportedOperationException("LVL-37 not implemented");
    }

    // LVL-38 · Дней между датами (может быть отрицательным, если перепутать порядок — учти в тесте выше: founded раньше today).
    static long daysSince(LocalDate from, LocalDate to) {
        // TODO LVL-38: ✍️ ChronoUnit.DAYS.between(from, to)
        throw new UnsupportedOperationException("LVL-38 not implemented");
    }

    // LVL-39 · Экранируй посимвольно: '|' → "\p", '\n' → "\n", '\' → "\\", остальное как есть.
    static String escape(String text) {
        // TODO LVL-39: ✍️ цикл по charAt, switch, StringBuilder
        throw new UnsupportedOperationException("LVL-39 not implemented");
    }

    static String unescape(String text) {
        // TODO LVL-40: ✍️ обратно: встретил '\' — смотри следующий символ (p→|, n→\n, \rightarrow\)
        throw new UnsupportedOperationException("LVL-40 not implemented");
    }

    // LVL-40 · Разбей по '|' но НЕ трогай экранированные "\p" (они должны остаться внутри кусков!).
    // Подсказка: встретил '\' — скопируй его И следующий символ целиком, не проверяя разделитель.
    static List<String> splitEscaped(String line) {
        // TODO LVL-40: ✍️ цикл + StringBuilder для текущего куска + ArrayList кусков
        throw new UnsupportedOperationException("LVL-40 not implemented");
    }
}
