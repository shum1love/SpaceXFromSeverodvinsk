package org.example.spacecompany.console;

/**
 * Цвета терминала (ANSI) с автовыключением.
 *
 * <p>Цвета включаются, только когда вывод идёт в живой терминал, который их
 * понимает. Выключаются автоматически, если: вывод перенаправлен в файл/пайп
 * (тогда в логах не будет мусора вида {@code [32m}), стоит {@code NO_COLOR},
 * терминал {@code dumb} или это старый Windows без поддержки ANSI.
 * Принудительно выключить: {@code NO_COLOR=1 java -jar ...}.
 *
 * <p>Правило: цвета — только здесь, в консольном слое. Сервисы и домен
 * возвращают чистый текст (те же строки показывает и JavaFX-интерфейс,
 * где ANSI-коды были бы мусором).
 */
public final class Ansi {

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GRAY = "\u001B[90m";

    private static final boolean ENABLED = detect();

    private Ansi() {
    }

    private static boolean detect() {
        String noColor = System.getenv("NO_COLOR");
        if (noColor != null && !noColor.isEmpty()) {
            return false;
        }
        if (System.console() == null) {
            return false;
        }
        if ("dumb".equals(System.getenv("TERM"))) {
            return false;
        }
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            return System.getenv("WT_SESSION") != null
                    || System.getenv("ANSICON") != null
                    || System.getenv("TERM_PROGRAM") != null;
        }
        return true;
    }

    /** Красит текст, если цвета включены; иначе возвращает как есть. */
    public static String paint(String text, String code) {
        if (!ENABLED || text == null) {
            return text;
        }
        return code + text + RESET;
    }

    public static String bold(String text) {
        return paint(text, BOLD);
    }

    public static String dim(String text) {
        return paint(text, DIM);
    }

    /** Раскрашивает слово статуса: хорошо — зелёным, плохо — красным и т.д. */
    public static String status(String statusName) {
        String code = switch (statusName) {
            case "READY", "SUCCESS", "AVAILABLE" -> GREEN;
            case "IN_PROGRESS", "ON_MISSION" -> CYAN;
            case "NO_FUEL", "IN_MAINTENANCE", "ON_LEAVE", "PLANNED" -> YELLOW;
            case "DAMAGED" -> YELLOW;
            case "DESTROYED", "FAILED", "CANCELLED" -> RED;
            default -> GRAY;
        };
        return paint(statusName, BOLD + code);
    }
}
