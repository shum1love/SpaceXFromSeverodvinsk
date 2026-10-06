// ============================================================
// QUEST 27 · ИНЖЕНЕР · Generics вглубь: границы, PECS, стирание
// Уровни ROADMAP: LVL 26В
//
//   ./q 27
//
// Три вопроса, на которые ответишь кодом: почему List<Integer> не есть
// List<Number>, зачем extends/super и почему нельзя new T().
// Максимум: 155 XP.
// ============================================================

import java.util.ArrayList;
import java.util.List;

public class Quest27_Generics {

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

    // Своя обобщённая коробка (как Box, но пишем сами — мышечная память!).
    static class Crate<T> {
        private T value;

        void put(T value) {
            // TODO 1а: ✍️ this.value = value;
            throw new UnsupportedOperationException("1a not implemented");
        }

        T get() {
            // TODO 1б: ✍️ return value;
            throw new UnsupportedOperationException("1b not implemented");
        }
    }

    // Пара с переворотом типов.
    static class Pair<K, V> {
        final K first;
        final V second;

        Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 27 · GENERICS ===");

        try {
            Crate<String> crate = new Crate<>();
            crate.put("звезда");
            check(crate.get().equals("звезда"), "1 · своя коробка Crate<T>", 15);
        } catch (UnsupportedOperationException e) {
            todo("1 · своя коробка Crate<T>");
        }
        try {
            check(first(List.of("a", "b")).equals("a"), "2 · generic-метод first", 20);
        } catch (UnsupportedOperationException e) {
            todo("2 · generic-метод first");
        }
        try {
            check(sum(List.of(1, 2, 3)) == 6.0 && sum(List.of(1.5, 2.5)) == 4.0,
                    "3 · граница <T extends Number>", 20);
        } catch (UnsupportedOperationException e) {
            todo("3 · граница <T extends Number>");
        }
        try {
            List<Integer> src = new ArrayList<>(List.of(1, 2));
            List<Number> dst = new ArrayList<>();
            copyAll(src, dst);
            check(dst.equals(List.of(1, 2)), "4 · PECS: copy extends→super", 25);
        } catch (UnsupportedOperationException e) {
            todo("4 · PECS: copy extends→super");
        }
        try {
            check(countAll(List.of("a", "b", "c")) == 3, "5 · wildcard-чтение List<?>", 15);
        } catch (UnsupportedOperationException e) {
            todo("5 · wildcard-чтение List<?>");
        }
        try {
            check(maxOf(3, 9) == 9 && maxOf("б", "а").equals("б"), "6 · <T extends Comparable<T>>", 20);
        } catch (UnsupportedOperationException e) {
            todo("6 · <T extends Comparable<T>>");
        }
        try {
            Pair<String, Integer> p = new Pair<>("ракет", 3);
            Pair<Integer, String> flipped = flip(p);
            check(flipped.first == 3 && flipped.second.equals("ракет"), "7 · переворот пары", 20);
        } catch (UnsupportedOperationException e) {
            todo("7 · переворот пары");
        }
        try {
            check(filled(3, "x").equals(List.of("x", "x", "x")), "8 · фабрика без new T()", 20);
        } catch (UnsupportedOperationException e) {
            todo("8 · фабрика без new T()");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 155");
    }

    // 2 · Первый элемент ЛЮБОГО списка. Подсказка: static <T> T first(List<T> list).
    static <T> T first(List<T> list) {
        // TODO 2: ✍️ return list.get(0);
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · Сумма чисел ЛЮБОГО числового типа. Без границы не вызвать doubleValue()!
    static <T extends Number> double sum(List<T> numbers) {
        // TODO 3: ✍️ суммируй через doubleValue (граница разрешает!). Аккумулятор какого типа?
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · Копирование: читаем из producer'а (extends), пишем в consumer'а (super). PECS!
    static <T> void copyAll(List<? extends T> src, List<? super T> dst) {
        // TODO 4: ✍️ тело — один цикл. Главное — прочитай СИГНАТУРУ выше и пойми, почему там extends/super.
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · Размер списка НЕИЗВЕСТНОГО типа: читать можно, писать (кроме null) — нет.
    static int countAll(List<?> list) {
        // TODO 5: ✍️ return list.size();
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · Максимум двух сравнимых. compareTo возвращает отрицательное/0/положительное —
    // сравни с нулём! (Что такое natural ordering — разберём на LVL 27Б, пока просто используй.)
    static <T extends Comparable<T>> T maxOf(T a, T b) {
        // TODO 6: ✍️ одна строка
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · Переверни пару: Pair<K,V> → Pair<V,K>.
    static <K, V> Pair<V, K> flip(Pair<K, V> pair) {
        // TODO 7: ✍️ return new Pair<>(pair.second, pair.first);
        throw new UnsupportedOperationException("7 not implemented");
    }

    // 8 · Список из n копий значения. new T() запрещён стиранием — передаём ГОТОВОЕ значение!
    static <T> List<T> filled(int n, T value) {
        // TODO 8: ✍️ заполни список в цикле. Массив new T[] запрещён стиранием — чем заменяем?
        throw new UnsupportedOperationException("8 not implemented");
    }
}
