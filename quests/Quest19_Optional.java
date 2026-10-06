// ============================================================
// QUEST 19 · ШТУРМАН · Optional: коробка "может отсутствовать"
// Уровни ROADMAP: LVL 32В, 32Г
//
//   ./q 19
//
// Две главные темы: orElse VS orElseGet (ленивость!) и цепочки
// map/flatMap/filter. Плюс честный список "когда Optional НЕ нужен".
// Максимум: 150 XP.
// ============================================================

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Quest19_Optional {

    static int xp = 0;
    static int calls = 0;

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

    // Дорогое значение по умолчанию (считаем вызовы!).
    static String expensiveDefault() {
        calls++;
        return "дефолт";
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 19 · OPTIONAL ===");

        try {
            check(wrap(null).isEmpty() && wrap("x").isPresent(), "1 · ofNullable и пустота", 15);
        } catch (UnsupportedOperationException e) {
            todo("1 · ofNullable и пустота");
        }
        try {
            check(pick(List.of("a"), 5).equals("a") && pick(List.of("a"), 9).equals("нет"),
                    "2 · findFirst + orElse", 15);
        } catch (UnsupportedOperationException e) {
            todo("2 · findFirst + orElse");
        }
        try {
            calls = 0;
            check(lazy(true).equals("есть") && calls == 0, "3 · orElseGet ленивый: дефолт НЕ строился", 25);
        } catch (UnsupportedOperationException e) {
            todo("3 · orElseGet ленивый: дефолт НЕ строился");
        }
        try {
            calls = 0;
            check(eager(true).equals("есть") && calls == 1, "4 · orElse жадный: дефолт построился зря!", 20);
        } catch (UnsupportedOperationException e) {
            todo("4 · orElse жадный: дефолт построился зря!");
        }
        try {
            check(shout(Optional.of("союз")).equals("СОЮЗ") && shout(Optional.empty()).equals("НЕТ"),
                    "5 · map-цепочка", 20);
        } catch (UnsupportedOperationException e) {
            todo("5 · map-цепочка");
        }
        try {
            check(flatName(Optional.of(Optional.of("Анна"))).equals("Анна")
                    && flatName(Optional.of(Optional.empty())).equals("НЕТ"),
                    "6 · flatMap разворачивает", 20);
        } catch (UnsupportedOperationException e) {
            todo("6 · flatMap разворачивает");
        }
        try {
            check(strongOnly(8).isPresent() && strongOnly(3).isEmpty(), "7 · filter", 15);
        } catch (UnsupportedOperationException e) {
            todo("7 · filter");
        }
        try {
            boolean thrown = false;
            try {
                mustFind(Optional.empty());
            } catch (NoSuchElementException e) {
                thrown = true;
            }
            check(thrown && mustFind(Optional.of(7)) == 7, "8 · orElseThrow", 20);
        } catch (UnsupportedOperationException e) {
            todo("8 · orElseThrow");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 150");
    }

    // 1 · Оберни строку: null → пустая коробка. Подсказка: Optional.ofNullable(s).
    static Optional<String> wrap(String s) {
        // TODO 1: ✍️ одна строка
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 3 · Ленивый дефолт: present=true → "есть", expensiveDefault НЕ вызывается (calls==0!).
    static String lazy(boolean present) {
        // TODO 3: ✍️ Optional<String> box = present ? Optional.of("есть") : Optional.empty();
        // return box.orElseGet(Quest19_Optional::expensiveDefault);
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · Жадный дефолт: тот же код, но orElse(...) — expensiveDefault вызовется ВСЕГДА.
    static String eager(boolean present) {
        // TODO 4: ✍️ как в 3, но .orElse(expensiveDefault()) — и calls станет 1!
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 2 · Первый элемент с индексом >= minIdx или "нет".
    static String pick(List<String> names, int minIdx) {
        // TODO 2: ✍️ элемент под индексом minIdx или «нет». Собери сам: стрим + пропуск + поиск + запасное значение
        // Хм, skip(minIdx)? Нет! Нужен элемент ПОД ИНДЕКСОМ minIdx, если он есть.
        // Честно: if (minIdx < names.size()) return names.get(minIdx); return "нет";
        // Но задача про Optional — сделай через стрим: filter по индексу... Проще:
        // return minIdx < names.size() ? names.get(minIdx) : "нет"; — а Optional где?
        // Правильный Optional-путь: names.stream().skip(minIdx).findFirst().orElse("нет") —
        // skip отбрасывает первые minIdx, findFirst берёт следующий. Работает! Пиши так.
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 5 · Имя капсом или "НЕТ". Подсказка: map(String::toUpperCase).orElse("НЕТ").
    static String shout(Optional<String> name) {
        // TODO 5: ✍️ одна строка
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · Двойная коробка → одинарная. Подсказка: flatMap(x -> x).
    static String flatName(Optional<Optional<String>> nested) {
        // TODO 6: ✍️ разверни двойную коробку. Какой метод уплощает Optional<Optional<T>>?
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · Навык, только если >= 5. Подсказка: Optional.of(skill).filter(s -> s >= 5).
    static Optional<Integer> strongOnly(int skill) {
        // TODO 7: ✍️ одна строка
        throw new UnsupportedOperationException("7 not implemented");
    }

    // 8 · Достань значение или брось NoSuchElementException с текстом.
    static int mustFind(Optional<Integer> box) {
        // TODO 8: ✍️ return box.orElseThrow();
        throw new UnsupportedOperationException("8 not implemented");
    }
}
