// ============================================================
// QUEST 22 · СВЯЗИСТ · Файлы: NIO, Properties, конфиги
// Уровни ROADMAP: LVL 39В, 39Г
//
//   ./q 22
//
// Всё крутится во ВРЕМЕННОЙ папке (ничего не засоряем!): пишем, читаем,
// гуляем по дереву, грузим .properties. Это же пригодится для сейвов.
// Максимум: 140 XP.
// ============================================================

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public class Quest22_Files {

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

    public static void main(String[] args) throws Exception {
        System.out.println("=== QUEST 22 · ФАЙЛЫ ===");

        try {
            check(roundTrip("привет, сейв!").equals("привет, сейв!"), "1 · записал-прочитал", 20);
        } catch (UnsupportedOperationException e) {
            todo("1 · записал-прочитал");
        }
        try {
            check(nestedDirs(), "2 · вложенные папки", 15);
        } catch (UnsupportedOperationException e) {
            todo("2 · вложенные папки");
        }
        try {
            check(countTxt() == 2, "3 · Files.list + фильтр .txt", 20);
        } catch (UnsupportedOperationException e) {
            todo("3 · Files.list + фильтр .txt");
        }
        try {
            check(walkAll() == 4, "4 · Files.walk считает рекурсивно", 25);
        } catch (UnsupportedOperationException e) {
            todo("4 · Files.walk считает рекурсивно");
        }
        try {
            check(copyUpper(List.of("a", "b")).equals(List.of("A", "B")), "5 · построчное копирование", 20);
        } catch (UnsupportedOperationException e) {
            todo("5 · построчное копирование");
        }
        try {
            check(configValue("fuel.price=2\n", "fuel.price", "?").equals("2")
                    && configValue("", "fuel.price", "99").equals("99"),
                    "6 · Properties + значение по умолчанию", 20);
        } catch (UnsupportedOperationException e) {
            todo("6 · Properties + значение по умолчанию");
        }
        try {
            check(appendLog().equals(List.of("старт", "полёт")), "7 · дозапись APPEND", 20);
        } catch (UnsupportedOperationException e) {
            todo("7 · дозапись APPEND");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 140");
    }

    static Path tmp() throws Exception {
        return Files.createTempDirectory("gym");
    }

    // 1 · Запиши текст в файл, прочитай обратно. Подсказка: writeString/readString + UTF_8.
    static String roundTrip(String text) throws Exception {
        // TODO 1: ✍️ Path f = tmp().resolve("save.txt");
        // Files.writeString(f, text, StandardCharsets.UTF_8); return Files.readString(f, StandardCharsets.UTF_8);
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · Создай a/b/c одной командой, верни Files.isDirectory(.../a/b/c).
    static boolean nestedDirs() throws Exception {
        // TODO 2: ✍️ Path deep = tmp().resolve("a/b/c"); Files.createDirectories(deep);
        // return Files.isDirectory(deep);
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · Создай a.txt, b.txt, c.log. Сколько .txt? Подсказка: Files.list + filter + count.
    // ВАЖНО: стрим Files.list — ресурс! Закрой через try-with-resources.
    static long countTxt() throws Exception {
        // TODO 3: ✍️ Path dir = tmp(); Files.writeString(dir.resolve("a.txt"), "1");
        // Files.writeString(dir.resolve("b.txt"), "2"); Files.writeString(dir.resolve("c.log"), "3");
        // try (Stream<Path> s = Files.list(dir)) { return s.filter(p -> p.toString().endsWith(".txt")).count(); }
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · Создай root/x.txt и root/sub/y.txt, root/sub/deep/z.txt + root/w.txt = 4 файла.
    // Посчитай ВСЕ файлы рекурсивно через Files.walk. Ловушка: walk видит и ПАПКИ — фильтруй isRegularFile!
    static long walkAll() throws Exception {
        // TODO 4: ✍️ построй дерево выше; try (Stream<Path> s = Files.walk(root)) {
        // return s.filter(Files::isRegularFile).count(); }
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · Скопируй строки в новый файл, подняв регистр. Подсказка: readAllLines + write.
    static List<String> copyUpper(List<String> lines) throws Exception {
        // TODO 5: ✍️ запиши вход, прочитай, подними регистр, запиши выход, верни прочитанное.
        // Понадобится import ArrayList — добавь сам!
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · Прочитай значение из текста .properties; нет ключа — верни default.
    // Подсказка: Properties p = new Properties(); p.load(new StringReader(text)); return p.getProperty(key, def);
    static String configValue(String text, String key, String def) throws Exception {
        // TODO 6: ✍️ три строки выше
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · Пиши "старт", потом ДОПИШИ "полёт" (APPEND!), прочитай обе строки списком.
    static List<String> appendLog() throws Exception {
        // TODO 7: ✍️ Path f = tmp().resolve("log.txt");
        // Files.writeString(f, "старт\n", UTF_8); Files.writeString(f, "полёт\n", UTF_8, StandardOpenOption.APPEND);
        // return Files.readAllLines(f, UTF_8);
        throw new UnsupportedOperationException("7 not implemented");
    }
}
