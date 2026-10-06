// ============================================================
// QUEST 18 · ШТУРМАН · Stream API: конвейеры и коллекторы
// Уровни ROADMAP: LVL 30В, 30Г, 30Д, 30Е, 30Ж (мини-босс)
//
//   ./q 18
//
// Правило файла: один конвейер — одна мысль. Промежуточные операции ленивы,
// запускает всё терминальная. Стрим одноразовый (задача D2 это докажет делом).
// Максимум: 295 XP.
// ============================================================

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Quest18_Streams {

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

    record Cadet(String role, int skill) {
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 18 · STREAMS ===");
        List<String> names = List.of("Анна", "Борис", "Вера", "Глеб");
        List<Integer> nums = List.of(5, 2, 8, 2, 9, 1);

        try {
            check(namesOfStrong(List.of(new Cadet("p", 8), new Cadet("e", 4))).equals(List.of("P-8")),
                    "A1 · filter + map", 20);
        } catch (UnsupportedOperationException e) {
            todo("A1 · filter + map");
        }
        try {
            check(distinctSorted(nums).equals(List.of(1, 2, 5, 8, 9)), "A2 · distinct + sorted", 15);
        } catch (UnsupportedOperationException e) {
            todo("A2 · distinct + sorted");
        }
        try {
            check(flatten(List.of(List.of(1, 2), List.of(3))).equals(List.of(1, 2, 3)), "A3 · flatMap", 20);
        } catch (UnsupportedOperationException e) {
            todo("A3 · flatMap");
        }
        try {
            check(page(nums, 2, 2).equals(List.of(8, 2)), "A4 · skip + limit (пагинация!)", 15);
        } catch (UnsupportedOperationException e) {
            todo("A4 · skip + limit (пагинация!)");
        }
        try {
            check(peeked(nums).equals(List.of(5, 2, 8, 2, 9, 1)), "A5 · peek подсматривает", 15);
        } catch (UnsupportedOperationException e) {
            todo("A5 · peek подсматривает");
        }
        try {
            check(anyLong(names) && allShort() && noneEmpty(names), "B1 · match-трио", 20);
        } catch (UnsupportedOperationException e) {
            todo("B1 · match-трио");
        }
        try {
            check(firstLong(names).equals("Борис"), "B2 · findFirst + orElse", 15);
        } catch (UnsupportedOperationException e) {
            todo("B2 · findFirst + orElse");
        }
        try {
            check(product(List.of(2, 3, 4)) == 24, "B3 · reduce", 20);
        } catch (UnsupportedOperationException e) {
            todo("B3 · reduce");
        }
        try {
            check(countLong(names) == 2L, "B4 · count", 10);
        } catch (UnsupportedOperationException e) {
            todo("B4 · count");
        }
        try {
            check(byId(List.of("a", "bb")).equals(Map.of("a", 1, "bb", 2)), "C1 · toMap", 20);
        } catch (UnsupportedOperationException e) {
            todo("C1 · toMap");
        }
        try {
            check(groupByLen(List.of("a", "bb", "c")).get(1).size() == 2, "C2 · groupingBy", 25);
        } catch (UnsupportedOperationException e) {
            todo("C2 · groupingBy");
        }
        try {
            var part = splitEvenOdd(List.of(1, 2, 3, 4));
            check(part.get(true).equals(List.of(2, 4)) && part.get(false).equals(List.of(1, 3)),
                    "C3 · partitioningBy", 20);
        } catch (UnsupportedOperationException e) {
            todo("C3 · partitioningBy");
        }
        try {
            check(joinNames(List.of("А", "Б")).equals("А, Б"), "C4 · joining", 15);
        } catch (UnsupportedOperationException e) {
            todo("C4 · joining");
        }
        try {
            check(avgSkill(List.of(new Cadet("p", 6), new Cadet("e", 9))) == 7.5, "D1 · mapToInt + average", 15);
        } catch (UnsupportedOperationException e) {
            todo("D1 · mapToInt + average");
        }
        try {
            reuse();
            check(false, "D2 · повторное использование (не должно дойти)", 0);
        } catch (IllegalStateException e) {
            check(true, "D2 · стрим одноразовый — видишь ISE!", 20);
        } catch (UnsupportedOperationException e) {
            todo("D2 · стрим одноразовый — видишь ISE!");
        }
        try {
            List<Cadet> team = List.of(new Cadet("pilot", 8), new Cadet("pilot", 4),
                    new Cadet("engineer", 9), new Cadet("medic", 3));
            check(topRoles(team).equals("engineer,pilot"), "BOSS · группировка + фильтр + джойн", 30);
        } catch (UnsupportedOperationException e) {
            todo("BOSS · группировка + фильтр + джойн");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 295");
    }

    // A1 · Имена сильных (skill>5) как "P-8": заглавная роль + "-" + скилл.
    static List<String> namesOfStrong(List<Cadet> team) {
        // TODO A1: ✍️ отфильтруй сильных (skill > 5), преобразуй в формат из проверки выше. Формат собери сам!
        throw new UnsupportedOperationException("A1 not implemented");
    }

    // A2 · Уникальные по возрастанию.
    static List<Integer> distinctSorted(List<Integer> nums) {
        // TODO A2: ✍️ убери дубликаты, отсортируй. Два вызова подряд — порядок важен?
        throw new UnsupportedOperationException("A2 not implemented");
    }

    // A3 · Список списков → плоский список. Подсказка: flatMap(List::stream).
    static List<Integer> flatten(List<List<Integer>> nested) {
        // TODO A3: ✍️ сплюсни: какой метод превращает стрим списков в стрим элементов?
        throw new UnsupportedOperationException("A3 not implemented");
    }

    // A4 · Страница: пропустить skip, взять limit.
    static List<Integer> page(List<Integer> nums, int skip, int limit) {
        // TODO A4: ✍️ пропусти skip, возьми limit — именно в этом порядке!
        throw new UnsupportedOperationException("A4 not implemented");
    }

    // A5 · peek НЕ меняет элементы — он подсматривает. Верни тот же список через peek.
    static List<Integer> peeked(List<Integer> nums) {
        // TODO A5: ✍️ передай пустую лямбду в peek и верни toList. Зачем тогда peek? (только отладка!)
        throw new UnsupportedOperationException("A5 not implemented");
    }

    // B1 · Три вопроса: есть ли имя длиннее 4? все ли короче 10? нет ли пустых?
    // Подсказки: anyMatch(s -> s.length() > 4); allMatch(...); noneMatch(String::isEmpty).
    static boolean anyLong(List<String> names) {
        // TODO B1a: ✍️ есть ли хоть одно длиннее 4? Подбери match-метод сам
        throw new UnsupportedOperationException("B1a not implemented");
    }

    static boolean allShort() {
        // TODO B1b: ✍️ все ли короче 10? Другой match-метод!
        throw new UnsupportedOperationException("B1b not implemented");
    }

    static boolean noneEmpty(List<String> names) {
        // TODO B1c: ✍️ нет ли пустых? Третий match-метод + method reference
        throw new UnsupportedOperationException("B1c not implemented");
    }

    // B2 · Первое имя длиннее 4 или "нет". Подсказка: findFirst() даёт Optional!
    static String firstLong(List<String> names) {
        // TODO B2: ✍️ отфильтруй длинные, возьми первый, запасное слово — через Optional-метод
        throw new UnsupportedOperationException("B2 not implemented");
    }

    // B3 · Произведение через reduce: (a, b) -> a * b, старт 1.
    static int product(List<Integer> nums) {
        // TODO B3: ✍️ сверни умножением. С какого стартового значения начать?
        throw new UnsupportedOperationException("B3 not implemented");
    }

    // B4 · Сколько имён длиннее 4. Подсказка: filter + count() (long!).
    static long countLong(List<String> names) {
        // TODO B4: ✍️ отфильтруй + посчитай. Тип результата? (подсказка: long!)
        throw new UnsupportedOperationException("B4 not implemented");
    }

    // C1 · Карта "имя→длина". Подсказка: toMap(s -> s, String::length).
    static Map<String, Integer> byId(List<String> names) {
        // TODO C1: ✍️ собери карту «имя→длина». Какой коллектор строит Map?
        throw new UnsupportedOperationException("C1 not implemented");
    }

    // C2 · Группировка по длине: Map<длина, список>. Подсказка: groupingBy(String::length).
    static Map<Integer, List<String>> groupByLen(List<String> names) {
        // TODO C2: ✍️ сгруппируй по длине. Коллектор называется как действие!
        throw new UnsupportedOperationException("C2 not implemented");
    }

    // C3 · Разбей на чёт/нечет: partitioningBy(n -> n % 2 == 0) → Map<Boolean, List>.
    static Map<Boolean, List<Integer>> splitEvenOdd(List<Integer> nums) {
        // TODO C3: ✍️ разбей на две кучи по чётности. Какой коллектор всегда даёт true/false ключи?
        throw new UnsupportedOperationException("C3 not implemented");
    }

    // C4 · Склей через ", ". Подсказка: joining(", ").
    static String joinNames(List<String> names) {
        // TODO C4: ✍️ склей с разделителем. Коллектор для строк — какой?
        throw new UnsupportedOperationException("C4 not implemented");
    }

    // D1 · Средний скилл через примитивный стрим (без боксинга!).
    static double avgSkill(List<Cadet> team) {
        // TODO D1: ✍️ примитивный стрим (без боксинга!) + среднее + запасное значение
        throw new UnsupportedOperationException("D1 not implemented");
    }

    // D2 · Используй стрим ДВАЖДЫ — получишь IllegalStateException. Напиши и увидь!
    static long reuse() {
        // TODO D2: ✍️ var s = java.util.stream.Stream.of(1, 2, 3); s.count(); return s.count();
        throw new UnsupportedOperationException("D2 not implemented");
    }

    // BOSS · Роли со средним скиллом >= 6, отсортированные, через запятую.
    // Подсказка-цепочка: groupingBy(роль, averagingDouble) → entrySet стрим → filter >= 6
    // → map(ключ) → sorted → joining(","). Разбей на два оператора, не стесняйся!
    static String topRoles(List<Cadet> team) {
        // TODO BOSS: ✍️ два этапа. Этап 1: карта «роль→средний скилл» (какой downstream считает
        // среднее?). Этап 2: стрим по записям карты — фильтр, ключи, сорт, джойн.
        // Застрял — открой Hint'ы LVL 30Ж в ROADMAP (там раскрытие по шагам, −10% XP).
        throw new UnsupportedOperationException("BOSS not implemented");
    }
}
