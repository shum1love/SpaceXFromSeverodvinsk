// ============================================================
// QUEST 14 · ИНЖЕНЕР · Regex для AQA: Pattern, Matcher, группы
// Уровни ROADMAP: LVL 21Е
//
//   ./q 14
//
// Регулярки в AQA — это разбор логов, проверка ID, вытаскивание значений
// из ответов API. Учим самый ходовой минимум: matches, find, group.
// Максимум: 170 XP.
// ============================================================

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Quest14_Regex {

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
        System.out.println("=== QUEST 14 · REGEX ===");

        try {
            check(isRoverId("RV-42") && !isRoverId("RV-4X"),
                    "1 · matches: ID вида RV-две цифры", 20);
        } catch (UnsupportedOperationException e) {
            todo("1 · matches: ID вида RV-две цифры");
        }
        try {
            check(isEmail("pilot@sever.space") && !isEmail("pilot@sever"),
                    "2 · email: user@host.зона", 20);
        } catch (UnsupportedOperationException e) {
            todo("2 · email: user@host.зона");
        }
        try {
            check(extractNumbers("бак 200, груз 1500").equals(List.of("200", "1500")),
                    "3 · find: все числа из строки", 25);
        } catch (UnsupportedOperationException e) {
            todo("3 · find: все числа из строки");
        }
        try {
            check(logLevel("[ERROR] boom").equals("ERROR") && logLevel("[INFO] ok").equals("INFO"),
                    "4 · группа: уровень из лога", 25);
        } catch (UnsupportedOperationException e) {
            todo("4 · группа: уровень из лога");
        }
        try {
            check(apiValue("{\"reward\": 3000}", "reward").equals("3000"),
                    "5 · группы: значение из JSON-крошки", 25);
        } catch (UnsupportedOperationException e) {
            todo("5 · группы: значение из JSON-крошки");
        }
        try {
            check(normalizeSpaces("a  b\tc").equals("a b c"), "6 · \\s+ в деле", 15);
        } catch (UnsupportedOperationException e) {
            todo("6 · \\s+ в деле");
        }
        try {
            check(splitCsv("a,b,,c").length == 4, "7 · split сохраняет пустые", 20);
        } catch (UnsupportedOperationException e) {
            todo("7 · split сохраняет пустые");
        }
        try {
            check(hasCyrillic("Союз-1") && !hasCyrillic("Soyuz-1"), "8 · класс [а-яА-ЯёЁ]", 20);
        } catch (UnsupportedOperationException e) {
            todo("8 · класс [а-яА-ЯёЁ]");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 170");
    }

    // 1 · Строка целиком = "RV-" + ровно две цифры. Подсказка: String.matches("RV-\\d{2}").
    // Внимание: matches требует совпадения ВСЕЙ строки (не find!).
    static boolean isRoverId(String s) {
        // TODO 1: ✍️ return s.matches("RV-\\d{2}");
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · Простой email: буквы/цифры/._- + @ + хост с точкой. Ловушка: точка в regex = "любой символ"!
    static boolean isEmail(String s) {
        // TODO 2: ✍️ return s.matches("[\\w.-]+@[\\w-]+\\.[a-z]{2,}");
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · Все числа из строки по порядку. Подсказка: Pattern.compile("\\d+"), while (m.find()) add m.group().
    static List<String> extractNumbers(String s) {
        // TODO 3: ✍️ Matcher m = Pattern.compile("\\d+").matcher(s);
        // List<String> out = new ArrayList<>(); while (m.find()) out.add(m.group()); return out;
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · Уровень лога в [СКОБКАХ]: верни слово без скобок. Подсказка: группа "\\[(\\w+)\\]", group(1).
    static String logLevel(String line) {
        // TODO 4: ✍️ скобки в regex — спецсимволы, их экранируют. А слово внутри — группа: какая? (w+)
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · Значение числового поля из {"ключ": число}. Подсказка: собери шаблон из key!
    // "\"reward\"\\s*:\\s*(\\d+)". В Java-строке каждая кавычка и бэкслэш удваиваются — считай их!
    static String apiValue(String json, String key) {
        // TODO 5: ✍️ собери шаблон из key: кавычки + двоеточие + пробелы + группа цифр. Считай бэкслэши!
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · Схлопни любые пробелы/табы в один пробел. Подсказка: replaceAll("\\s+", " ").
    static String normalizeSpaces(String s) {
        // TODO 6: ✍️ одна строка
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · split(",") на "a,b,,c" даёт 4 куска (пустой сохраняется!). Просто верни split.
    // Ловушка: split отбрасывает пустые ХВОСТЫ ("a," → ["a"]). Запомни!
    static String[] splitCsv(String s) {
        // TODO 7: ✍️ return s.split(",");
        throw new UnsupportedOperationException("7 not implemented");
    }

    // 8 · Есть ли кириллица. Подсказка: "[а-яА-ЯёЁ]" + find (не matches!).
    static boolean hasCyrillic(String s) {
        // TODO 8: ✍️ return Pattern.compile("[а-яА-ЯёЁ]").matcher(s).find();
        throw new UnsupportedOperationException("8 not implemented");
    }
}
