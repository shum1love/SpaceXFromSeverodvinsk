// ============================================================
// QUEST 29 · АРЕНА · Ловушки интервью: неправильные ответы кандидатов
// Уровни ROADMAP: Арена LVL 55–66 (разбирай по мере блицев),
// точечно: LVL 10Г, 47Г, 49В (там ссылки на эти задачи)
//
//   ./q 29
//
// Формат: дан уверенно звучащий НЕПРАВИЛЬНЫЙ ответ. Твоя работа —
// опровергнуть его кодом (counterexample!) или точной формулировкой.
// Проверки принимают варианты ответов: пиши своими словами, но попади
// в ключевое слово. Сначала думай, потом запускай!
// Максимум: 190 XP.
// ============================================================

import java.util.ArrayList;
import java.util.List;

public class Quest29_Traps {

    static int xp = 0;
    static final List<String> CACHE = new ArrayList<>();

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

    /** Принимает ответ, если в нём есть хоть один вариант (регистр не важен). */
    static boolean says(String answer, String... variants) {
        if (answer == null) {
            return false;
        }
        String a = answer.trim().toLowerCase();
        for (String v : variants) {
            if (a.contains(v.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // Ключ с equals, но БЕЗ hashCode — классика поломанного контракта.
    static class BadKey {
        final String id;

        BadKey(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof BadKey other && id.equals(other.id);
        }
        // hashCode НЕ переопределён — специально! Что сломается?
    }

    static class Parent {
        static String who() {
            return "parent-static";
        }

        String greet() {
            return "parent";
        }
    }

    static class Child extends Parent {
        static String who() {
            return "child-static";
        }

        @Override
        String greet() {
            return "child";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 29 · ЛОВУШКИ ===");

        try {
            check(says(passAnswer(), "значен", "value"), "1 · миф: «объекты по ссылке»", 15);
        } catch (UnsupportedOperationException e) {
            todo("1 · миф: «объекты по ссылке»");
        }
        try {
            check(says(volatileFixes(), "видимост", "visibility"), "2 · миф: volatile чинит всё", 20);
        } catch (UnsupportedOperationException e) {
            todo("2 · миф: volatile чинит всё");
        }
        try {
            check(says(volatileCounter(), "три", "read-modify-write", "read", "неатомар"),
                    "3 · миф: volatile делает ++ атомарным", 20);
        } catch (UnsupportedOperationException e) {
            todo("3 · миф: volatile делает ++ атомарным");
        }
        try {
            check(twins() && !twinsEqual(), "4 · миф: равный hash = равные объекты", 20);
        } catch (UnsupportedOperationException e) {
            todo("4 · миф: равный hash = равные объекты");
        }
        try {
            check(finalArray() == 2, "5 · миф: final = immutable", 15);
        } catch (UnsupportedOperationException e) {
            todo("5 · миф: final = immutable");
        }
        try {
            check(pooledEq(), "6 · миф: == всегда про ссылки", 15);
        } catch (UnsupportedOperationException e) {
            todo("6 · миф: == всегда про ссылки");
        }
        try {
            check(says(finallySkipped(), "exit"), "7 · миф: finally выполняется всегда", 15);
        } catch (UnsupportedOperationException e) {
            todo("7 · миф: finally выполняется всегда");
        }
        try {
            int before = CACHE.size();
            check(leak(3) == before + 3 && CACHE.size() == before + 3,
                    "8 · миф: GC исключает утечки", 25);
        } catch (UnsupportedOperationException e) {
            todo("8 · миф: GC исключает утечки");
        }
        try {
            check(brokenGet(), "9 · миф: equals достаточно для HashMap", 25);
        } catch (UnsupportedOperationException e) {
            todo("9 · миф: equals достаточно для HashMap");
        }
        try {
            check(chain().equals("parent-static|child"), "10 · миф: static переопределяется", 20);
        } catch (UnsupportedOperationException e) {
            todo("10 · миф: static переопределяется");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 190");
    }

    // 1 · Кандидат: "Java передаёт объекты по ссылке". Опровергни ОДНИМ словом:
    // как на самом деле передаются аргументы? (Подсказка: Quest08, задачи 6–8.)
    static String passAnswer() {
        // TODO 1: ✍️ верни слово-ответ, например "по значению". Проверка ищет корень "значен"/"value".
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · Кандидат: "сделаю поле volatile — и никаких проблем с потоками".
    // А что volatile чинит НА САМОМ ДЕЛЕ? Одно слово!
    static String volatileFixes() {
        // TODO 2: ✍️ верни слово-ответ. Проверка ищет "видимост"/"visibility".
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · Кандидат: "volatile int counter; counter++ теперь атомарен".
    // Почему нет? Ответь фразой про устройство операции.
    static String volatileCounter() {
        // TODO 3: ✍️ верни фразу-ответ. Проверка ищет "три", "read-modify-write" или "неатомар".
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · Кандидат: "раз hashCode равные — значит, объекты равные".
    // Контрпример: знаменитая пара "Aa"/"BB"! Верни true, только если ОБА факта сошлись:
    // хэши равны, а equals — нет.
    static boolean twins() {
        // TODO 4: ✍️ return "Aa".hashCode() == "BB".hashCode();
        throw new UnsupportedOperationException("4 not implemented");
    }

    static boolean twinsEqual() {
        // TODO 4: ✍️ return "Aa".equals("BB");
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · Кандидат: "поле final — значит, объект immutable".
    // Контрпример: final-массив, у которого поменяли элемент. Верни a[0] после записи.
    static int finalArray() {
        // TODO 5: ✍️ final int[] a = {1}; a[0] = 2; return a[0]; — скомпилировалось? Вот именно.
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · Кандидат: "== всегда сравнивает ссылки, а значит для разных объектов всегда false".
    // Контрпример: два ЛИТЕРАЛА "abc" — один объект пула! Верни результат сравнения.
    static boolean pooledEq() {
        // TODO 6: ✍️ String a = "abc"; String b = "abc"; return a == b; — true! Пул, детка.
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · Кандидат: "finally выполняется АБСОЛЮТНО всегда".
    // А вот и нет: назови способ убить JVM так, что finally не запустится. Одно слово!
    static String finallySkipped() {
        // TODO 7: ✍️ верни слово-ответ. Проверка ищет "exit" (System.exit / крах JVM / бесконечный цикл тоже сойдут в объяснении).
        throw new UnsupportedOperationException("7 not implemented");
    }

    // 8 · Кандидат: "в Java утечек памяти не бывает — есть же GC".
    // Контрпример: статическое хранилище, которое только растёт. Допиши утечку:
    // добавь n записей в CACHE и верни новый размер. GC их НЕ заберёт — они достижимы!
    static int leak(int n) {
        // TODO 8: ✍️ for (int i = 0; i < n; i++) CACHE.add("мусор-" + i); return CACHE.size();
        // Бонус-вопрос себе: что здесь GC root? (ответ: static-поле класса!)
        throw new UnsupportedOperationException("8 not implemented");
    }

    // 9 · Кандидат: "для HashMap достаточно переопределить equals".
    // Контрпример: два РАВНЫХ BadKey обязаны иметь равные hashCode (контракт!).
    // Верни true, если контракт НАРУШЕН (хэши разные) — тогда и HashMap их потеряет.
    static boolean brokenGet() {
        // TODO 9: ✍️ верни (new BadKey("x").hashCode() != new BadKey("x").hashCode()).
        // Дефолтный hashCode — от identity, а не от id, поэтому у равных объектов хэши разные!
        throw new UnsupportedOperationException("9 not implemented");
    }

    // 10 · Кандидат: "static-метод переопределяется, как обычный".
    // Правда: static — hiding по типу ССЫЛКИ, instance — override по объекту!
    // Собери строку "X|Y": X = Parent p = new Child(); p.who(), Y = p.greet().
    static String chain() {
        // TODO 10: ✍️ Parent p = new Child(); return p.who() + "|" + p.greet();
        // Предскажи ДО запуска: "parent-static|child". Почему static не "child-static"?
        throw new UnsupportedOperationException("10 not implemented");
    }
}
