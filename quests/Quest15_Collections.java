// ============================================================
// QUEST 15 · ИНЖЕНЕР · Коллекции: выбор, скорость, итераторы, утилиты
// Уровни ROADMAP: LVL 23В, 23Г, 24Б, 24В, 24Г, 26Б
//
//   ./q 15
//
// Главный навык файла — ВЫБОР структуры под задачу (это спрашивают всегда).
// Плюс две знаменитые ловушки: CME при удалении в цикле и "неизменяемые"
// коллекции, которые бросаются исключениями.
// Максимум: 245 XP.
// ============================================================

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

public class Quest15_Collections {

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
        System.out.println("=== QUEST 15 · КОЛЛЕКЦИИ ===");

        try {
            check(setFor("unique").equals("HashSet") && setFor("ordered").equals("LinkedHashSet")
                    && setFor("sorted").equals("TreeSet"), "A1 · какой Set и когда", 15);
        } catch (UnsupportedOperationException e) {
            todo("A1 · какой Set и когда");
        }
        try {
            check(queueFor("fifo").equals("ArrayDeque") && queueFor("stack").equals("ArrayDeque")
                    && queueFor("priority").equals("PriorityQueue"), "A2 · очереди и стек", 15);
        } catch (UnsupportedOperationException e) {
            todo("A2 · очереди и стек");
        }
        try {
            check(mapFor("fast").equals("HashMap") && mapFor("insertion").equals("LinkedHashMap")
                    && mapFor("sorted").equals("TreeMap"), "A3 · какой Map и когда", 15);
        } catch (UnsupportedOperationException e) {
            todo("A3 · какой Map и когда");
        }
        try {
            Deque<String> history = undoStack();
            check(history.removeFirst().equals("полёт-2") && history.removeFirst().equals("полёт-1"),
                    "B1 · Deque как стек отмены", 15);
        } catch (UnsupportedOperationException e) {
            todo("B1 · Deque как стек отмены");
        }
        try {
            List<Integer> scores = new ArrayList<>(List.of(10, 20, 30));
            check(fastReplace(scores) == 99 && scores.get(1) == 99, "B2 · ArrayList: замена по индексу", 15);
        } catch (UnsupportedOperationException e) {
            todo("B2 · ArrayList: замена по индексу");
        }
        try {
            List<Integer> data = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                data.add(i);
            }
            long t1 = System.nanoTime();
            long s1 = sumByIndex(new ArrayList<>(data));
            long t2 = System.nanoTime();
            long s2 = sumByIndex(new LinkedList<>(data));
            long t3 = System.nanoTime();
            System.out.println("    ArrayList: " + (t2 - t1) / 1000 + " мкс, LinkedList: " + (t3 - t2) / 1000 + " мкс");
            check(s1 == s2 && s1 == 499500L, "B3 · один код — две скорости (смотри цифры!)", 20);
        } catch (UnsupportedOperationException e) {
            todo("B3 · один код — две скорости (смотри цифры!)");
        }
        try {
            check(sortedUnique(new int[]{3, 1, 3, 2}).equals(List.of(1, 2, 3)), "C1 · TreeSet: сорт+уникальность", 20);
        } catch (UnsupportedOperationException e) {
            todo("C1 · TreeSet: сорт+уникальность");
        }
        try {
            check(new ArrayList<>(insertionOrder().keySet()).equals(List.of("b", "a", "c")),
                    "C2 · LinkedHashMap помнит порядок", 20);
        } catch (UnsupportedOperationException e) {
            todo("C2 · LinkedHashMap помнит порядок");
        }
        try {
            check(launchOrder().equals(List.of("Спутник", "Груз", "Марс")), "C3 · PriorityQueue: сначала лёгкие", 20);
        } catch (UnsupportedOperationException e) {
            todo("C3 · PriorityQueue: сначала лёгкие");
        }
        try {
            List<String> names = new ArrayList<>(List.of("Анна", "Бо", "Вера"));
            removeShortBuggy(names);
            check(false, "D1 · БАГ: удаление в for-each (не должно дойти)", 0);
        } catch (ConcurrentModificationException e) {
            check(true, "D1 · видишь CME? Это и есть баг!", 20);
        } catch (UnsupportedOperationException e) {
            todo("D1 · видишь CME? Это и есть баг!");
        }
        try {
            List<String> names = new ArrayList<>(List.of("Анна", "Бо", "Вера"));
            removeShortOk(names);
            check(names.equals(List.of("Анна", "Вера")), "D2 · чиним через Iterator.remove", 20);
        } catch (UnsupportedOperationException e) {
            todo("D2 · чиним через Iterator.remove");
        }
        try {
            check(minMax(new int[]{5, 1, 9}).equals("1/9"), "E1 · Collections.min/max", 15);
        } catch (UnsupportedOperationException e) {
            todo("E1 · Collections.min/max");
        }
        try {
            List<String> frozen = List.of("a");
            frozen.add("b");
            System.out.println("❌ E2 · List.of почему-то изменился?! Такого быть не должно");
        } catch (UnsupportedOperationException e) {
            System.out.println("👀 E2 · видишь UOE? List.of() неизменяем — запомни это исключение!");
        }
        try {
            check(mutableCopy().size() == 2, "E2 · чиним: growable копия List.of", 20);
        } catch (UnsupportedOperationException e) {
            todo("E2 · чиним: growable копия List.of");
        }
        try {
            check(freqOf(List.of("a", "b", "a"), "a") == 2, "E3 · Collections.frequency", 15);
        } catch (UnsupportedOperationException e) {
            todo("E3 · Collections.frequency");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 245");
    }

    // A1 · Какой Set: "unique"→HashSet, "ordered"→LinkedHashSet, "sorted"→TreeSet.
    static String setFor(String need) {
        // TODO A1: ✍️ switch по need, верни имя класса строкой
        throw new UnsupportedOperationException("A1 not implemented");
    }

    // A2 · "fifo"→ArrayDeque, "stack"→ArrayDeque, "priority"→PriorityQueue.
    static String queueFor(String need) {
        // TODO A2: ✍️ switch по need
        throw new UnsupportedOperationException("A2 not implemented");
    }

    // A3 · "fast"→HashMap, "insertion"→LinkedHashMap, "sorted"→TreeMap.
    static String mapFor(String need) {
        // TODO A3: ✍️ switch по need
        throw new UnsupportedOperationException("A3 not implemented");
    }

    // B1 · История действий как стек: положи "полёт-1", "полёт-2", верни стек.
    static Deque<String> undoStack() {
        // TODO B1: ✍️ клади в конец, забирай с начала. Какой Deque-метод забирает с начала? (не get!)
        throw new UnsupportedOperationException("B1 not implemented");
    }

    // B2 · Замени элемент с индексом 1 на 99, верни СТАРОЕ значение (set возвращает его!).
    static int fastReplace(List<Integer> scores) {
        // TODO B2: ✍️ замени элемент по индексу. Подвох: set ВОЗВРАЩАЕТ старое значение — используй это!
        throw new UnsupportedOperationException("B2 not implemented");
    }

    // B3 · Сумма через get(i). Код одинаков для ArrayList и LinkedList —
    // а время разное: get по индексу у LinkedList идёт по цепочке узлов!
    static long sumByIndex(List<Integer> data) {
        // TODO B3: ✍️ long s = 0; for (int i = 0; i < data.size(); i++) s += data.get(i); return s;
        throw new UnsupportedOperationException("B3 not implemented");
    }

    // C1 · Отсортированный список уникальных. Подсказка: new TreeSet<>() + addAll + new ArrayList<>(set).
    static List<Integer> sortedUnique(int[] a) {
        // TODO C1: ✍️ прогони массив через TreeSet (сортировка+уникальность даром!), верни списком
        throw new UnsupportedOperationException("C1 not implemented");
    }

    // C2 · Собери LinkedHashMap вставками b, a, c (значения любые). Порядок сохранится!
    static Map<String, Integer> insertionOrder() {
        // TODO C2: ✍️ вставляй в порядке b, a, c. Порядок сохранится сам — проверь ключами!
        throw new UnsupportedOperationException("C2 not implemented");
    }

    // Готовый инструмент сравнения (как он устроен изнутри — разберём на LVL 29,
    // пока используй как чёрный ящик: сравнивает пары по первому числу).
    static final java.util.Comparator<int[]> BY_WEIGHT = (a, b) -> a[0] - b[0];

    // C3 · Приоритет — лёгкие миссии вперёд. Веса: Марс 5000, Спутник 500, Груз 2000.
    // Направление: клади пары {вес, индекс} в очередь, извлекай poll() по порядку,
    // собирай названия. Компаратор BY_WEIGHT уже готов выше — просто передай в очередь!
    static List<String> launchOrder() {
        // TODO C3: ✍️ String[] names = {"Марс", "Спутник", "Груз"}; int[] w = {5000, 500, 2000};
        // PriorityQueue<int[]> q = new PriorityQueue<>(BY_WEIGHT);
        // положи {w[i], i}, извлекай poll() и собирай names[idx]
        throw new UnsupportedOperationException("C3 not implemented");
    }

    // D1 · БАГ НАМЕРЕННО: удаление в for-each ломается с CME. НЕ ЧИНИ — просто запусти и увидь!
    static void removeShortBuggy(List<String> names) {
        // TODO D1: ✍️ for (String n : names) { if (n.length() < 3) names.remove(n); }
        throw new UnsupportedOperationException("D1 not implemented");
    }

    // D2 · ЧИНИМ: тот же код через Iterator + it.remove(). CME больше нет.
    static void removeShortOk(List<String> names) {
        // TODO D2: ✍️ Iterator<String> it = names.iterator();
        // while (it.hasNext()) { if (it.next().length() < 3) it.remove(); }
        throw new UnsupportedOperationException("D2 not implemented");
    }

    // E2 · Лечим неизменяемость: оберни в new ArrayList<>() — и можно растить.
    static List<String> mutableCopy() {
        // TODO E2: ✍️ List<String> list = new ArrayList<>(List.of("a")); list.add("b"); return list;
        throw new UnsupportedOperationException("E2 not implemented");
    }

    // E3 · Частота через готовый метод Collections. Подсказка: Collections.frequency(list, value).
    static int freqOf(List<String> list, String value) {
        // TODO E3: ✍️ одна строка
        throw new UnsupportedOperationException("E3 not implemented");
    }

    // E1 · "min/max" через Collections.min/max. Подсказка: нужен List<Integer> из массива.
    static String minMax(int[] a) {
        // TODO E1: ✍️ List<Integer> list = new ArrayList<>(); заполни; return min + "/" + max;
        throw new UnsupportedOperationException("E1 not implemented");
    }
}
