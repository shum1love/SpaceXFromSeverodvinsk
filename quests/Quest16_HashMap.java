// ============================================================
// QUEST 16 · ИНЖЕНЕР · HashMap: корзина, коллизии, ловушки, практика
// Уровни ROADMAP: LVL 25В, 25Г, 25Д, 25Е, 25Ж (мини-босс)
//
//   ./q 16
//
// HashMap — главная interview-тема коллекций. Здесь: как устроен поиск,
// что бывает при коллизиях, почему ключ нельзя менять и много практики
// подсчёта/группировки (это же спрашивают на live-coding).
// Максимум: 220 XP.
// ============================================================

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Quest16_HashMap {

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

    // Ключ с КОНСТАНТНЫМ хэшем: все три лягут в ОДНУ корзину списком.
    // Поиск всё равно работает — но медленно. Это и есть коллизия.
    static class FixedHash {
        final String id;

        FixedHash(String id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof FixedHash other && id.equals(other.id);
        }
    }

    // Изменяемый ключ: id участвует в hashCode. Поменял поле — ключ "потерялся".
    static class MutableKey {
        int id;

        MutableKey(int id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof MutableKey other && id == other.id;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 16 · HASHMAP ===");

        try {
            Map<String, Integer> freq = wordFreq(new String[]{"a", "b", "a"});
            check(freq.get("a") == 2 && freq.get("b") == 1, "1 · частотная карта", 20);
        } catch (UnsupportedOperationException e) {
            todo("1 · частотная карта");
        }
        try {
            check(nullKeyOk(), "2 · null-ключ разрешён (один!)", 15);
        } catch (UnsupportedOperationException e) {
            todo("2 · null-ключ разрешён (один!)");
        }
        try {
            check(collisionOk().equals("1-2-3"), "3 · коллизия: одна корзина, всё находится", 25);
        } catch (UnsupportedOperationException e) {
            todo("3 · коллизия: одна корзина, всё находится");
        }
        try {
            check(lostKey(), "4 · ловушка: поменял ключ — get вернул null", 25);
        } catch (UnsupportedOperationException e) {
            todo("4 · ловушка: поменял ключ — get вернул null");
        }
        try {
            Map<Integer, List<String>> g = groupByLength(new String[]{"a", "bb", "c", "dd"});
            check(g.get(1).size() == 2 && g.get(2).size() == 2, "5 · группировка по длине", 25);
        } catch (UnsupportedOperationException e) {
            todo("5 · группировка по длине");
        }
        try {
            check(Arrays.equals(twoSum(new int[]{2, 7, 11, 15}, 9), new int[]{0, 1}),
                    "6 · two-sum за один проход", 25);
        } catch (UnsupportedOperationException e) {
            todo("6 · two-sum за один проход");
        }
        try {
            check(duplicates(new String[]{"a", "b", "a", "c", "b"}).equals(Set.of("a", "b")),
                    "7 · дубликаты через Set", 20);
        } catch (UnsupportedOperationException e) {
            todo("7 · дубликаты через Set");
        }
        try {
            Map<String, Integer> scores = new HashMap<>();
            addTo(scores, "Анна", 5);
            addTo(scores, "Анна", 3);
            check(scores.get("Анна") == 8, "8 · merge/getOrDefault: добавь очки", 20);
        } catch (UnsupportedOperationException e) {
            todo("8 · merge/getOrDefault: добавь очки");
        }
        try {
            check(uniqueCount(new String[]{"a", "b", "a"}) == 2, "9 · HashSet внутри — тот же HashMap", 15);
        } catch (UnsupportedOperationException e) {
            todo("9 · HashSet внутри — тот же HashMap");
        }
        try {
            String[] log = {"SUCCESS:1", "FAILED:2", "SUCCESS:3", "SUCCESS:4"};
            check(topStatus(log).equals("SUCCESS"), "10 · 👹 МИНИ-БОСС: топ-статус логов", 30);
        } catch (UnsupportedOperationException e) {
            todo("10 · 👹 МИНИ-БОСС: топ-статус логов");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 220");
    }

    // 1 · Частоты слов: getOrDefault(w, 0) + 1. Классика live-coding!
    static Map<String, Integer> wordFreq(String[] words) {
        // TODO 1: ✍️ для каждого слова увеличь его счётчик. Какой метод даёт 0 для новых слов?
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · HashMap терпит ОДИН null-ключ (в отличие от TreeMap/ConcurrentHashMap!). Докажи.
    static boolean nullKeyOk() {
        // TODO 2: ✍️ положи null-ключ и достань. А второй null-ключ — что будет? Проверь в отладчике!
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · Положи три FixedHash-ключа ("a"→1, "b"→2, "c"→3), достань все, склей значения через "-".
    static String collisionOk() {
        // TODO 3: ✍️ положи троих, достань по НОВЫМ объектам-ключам (new FixedHash("a")). Почему находится?
        // (Ответ: корзина одна, дальше ищет equals. Хэш сталкивает, equals различает!)
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · Положи MutableKey(1)→"груз", смени key.id = 100, попробуй достать. Верни (get(...) == null).
    static boolean lostKey() {
        // TODO 4: ✍️ положи ключ, ПОМЕНЯЙ его поле, попробуй достать. Что вернётся и почему? (подумай про корзину!)
        // (Ключ «уплыл» в другую корзину, а лежит в старой. Вывод: ключи — immutable!)
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · Сгруппируй слова по длине: Map<длина, список>. Подсказка: computeIfAbsent!
    static Map<Integer, List<String>> groupByLength(String[] words) {
        // TODO 5: ✍️ для каждого слова добавь его в список по его длине. Какой метод создаёт список при первом разе?
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · Two-sum: индексы пары с суммой target за ОДИН проход.
    // Подсказка: для каждого x ищи (target - x) в карте "значение→индекс", потом клади x.
    static int[] twoSum(int[] a, int target) {
        // TODO 6: ✍️ иди слева направо. Для x ищи пару (target-x) в карте «значение→индекс».
        // Нашёл — верни пару индексов; нет — запомни x и иди дальше. Один проход!
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · Какие значения повторяются. Подсказка: seen-set + dups-set.
    static Set<String> duplicates(String[] words) {
        // TODO 7: ✍️ два сета: увидел впервые — в seen, уже видел — в dups. Верни второй!
        // цикл: if (!seen.add(w)) dups.add(w); return dups;
        throw new UnsupportedOperationException("7 not implemented");
    }

    // 8 · Добавь delta к счёту игрока. Подсказка: merge(name, delta, Integer::sum)!
    static void addTo(Map<String, Integer> scores, String name, int delta) {
        // TODO 8: ✍️ прибавь delta одним вызовом, без if. Такой метод у Map есть (начинается на m...) — найди его сам!
        throw new UnsupportedOperationException("8 not implemented");
    }

    // 9 · Число уникальных: HashSet — это обёртка над HashMap (знаешь теперь!).
    static int uniqueCount(String[] words) {
        // TODO 9: ✍️ собери сет из списка и верни размер. Почему это работает? (сет внутри — мапа!)
        throw new UnsupportedOperationException("9 not implemented");
    }

    // 10 · МИНИ-БОСС: строки "СТАТУС:номер". Верни статус с наибольшим числом строк.
    // ДЛЯ СМЕЛЫХ: верни топ-3 в формате "A=3, B=2, C=1" (сортировка значений по убыванию).
    // Проверку допиши сам в main по образцу выше!
    // Подсказка: split(":") + wordFreq-идея из задачи 1, потом поиск максимума.
    static String topStatus(String[] log) {
        // TODO 10: ✍️ частоты статусов — как в задаче 1, только ключ = часть до «:». Потом найди ключ с max значением
        throw new UnsupportedOperationException("10 not implemented");
    }
}
