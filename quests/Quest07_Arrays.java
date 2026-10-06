// ============================================================
// QUEST 07 · КАДЕТ · Целочисленная арифметика и массивы
// Уровни ROADMAP: LVL 6В, 6Г, 9В–9Е
//
//   ./q 7
//
// Секция A — ловушки целочисленной арифметики (любимое на собеседованиях).
// Секция B — классика массивов: пиши руками, без Stream (они будут позже).
// Максимум: 295 XP.
// ============================================================

import java.util.Arrays;

public class Quest07_Arrays {

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
        System.out.println("=== QUEST 07 · МАССИВЫ ===");

        try {
            check(intDiv() == 3, "A1 · целочисленное деление 7/2", 15);
        } catch (UnsupportedOperationException e) {
            todo("A1 · целочисленное деление 7/2");
        }
        try {
            check(overflows() == Integer.MIN_VALUE, "A2 · переполнение MAX_VALUE+1", 15);
        } catch (UnsupportedOperationException e) {
            todo("A2 · переполнение MAX_VALUE+1");
        }
        try {
            check(castTrap() == 1, "A3 · кастинг (int)(2.9/2)", 15);
        } catch (UnsupportedOperationException e) {
            todo("A3 · кастинг (int)(2.9/2)");
        }
        try {
            check(negMod() == -1, "A4 · остаток отрицательного -7%3", 15);
        } catch (UnsupportedOperationException e) {
            todo("A4 · остаток отрицательного -7%3");
        }
        try {
            check(incTrap() == 12, "A5 · ловушка i++ + ++i при i=5", 20);
        } catch (UnsupportedOperationException e) {
            todo("A5 · ловушка i++ + ++i при i=5");
        }
        try {
            check(sum(new int[]{1, 2, 3, 4}) == 10, "B1 · сумма", 15);
        } catch (UnsupportedOperationException e) {
            todo("B1 · сумма");
        }
        try {
            check(Math.abs(average(new int[]{1, 2, 3, 4}) - 2.5) < 1e-9, "B2 · среднее (double!)", 15);
        } catch (UnsupportedOperationException e) {
            todo("B2 · среднее (double!)");
        }
        try {
            check(range(new int[]{5, 1, 9, 3}) == 8, "B3 · размах max-min", 15);
        } catch (UnsupportedOperationException e) {
            todo("B3 · размах max-min");
        }
        try {
            check(Arrays.equals(reversed(new int[]{1, 2, 3}), new int[]{3, 2, 1}), "B4 · разворот", 20);
        } catch (UnsupportedOperationException e) {
            todo("B4 · разворот");
        }
        try {
            check(hasDuplicates(new int[]{1, 2, 3, 2}) && !hasDuplicates(new int[]{1, 2, 3}),
                    "B5 · есть ли дубликаты", 20);
        } catch (UnsupportedOperationException e) {
            todo("B5 · есть ли дубликаты");
        }
        try {
            check(secondMax(new int[]{5, 1, 9, 3, 9}) == 5, "B6 · второй максимум", 20);
        } catch (UnsupportedOperationException e) {
            todo("B6 · второй максимум");
        }
        try {
            check(indexOf(new int[]{4, 7, 9}, 7) == 1 && indexOf(new int[]{4, 7, 9}, 5) == -1,
                    "B7 · индекс элемента", 15);
        } catch (UnsupportedOperationException e) {
            todo("B7 · индекс элемента");
        }
        try {
            check(frequency(new int[]{1, 2, 2, 3, 2}, 2) == 3, "B8 · частота значения", 15);
        } catch (UnsupportedOperationException e) {
            todo("B8 · частота значения");
        }
        try {
            check(Arrays.equals(merge(new int[]{1, 2}, new int[]{3, 4}), new int[]{1, 2, 3, 4}), "B9 · склейка", 20);
        } catch (UnsupportedOperationException e) {
            todo("B9 · склейка");
        }
        try {
            check(Arrays.equals(intersection(new int[]{1, 2, 3}, new int[]{2, 3, 4}), new int[]{2, 3}),
                    "B10 · пересечение", 20);
        } catch (UnsupportedOperationException e) {
            todo("B10 · пересечение");
        }
        try {
            check(matrixSum(new int[][]{{1, 2}, {3, 4}}) == 10, "B11 · сумма матрицы", 20);
        } catch (UnsupportedOperationException e) {
            todo("B11 · сумма матрицы");
        }
        try {
            int[] src = {3, 1, 2};
            int[] sorted = sortedCopy(src);
            check(Arrays.equals(sorted, new int[]{1, 2, 3}) && Arrays.equals(src, new int[]{3, 1, 2}),
                    "B12 · отсортированная копия (оригинал цел!)", 20);
        } catch (UnsupportedOperationException e) {
            todo("B12 · отсортированная копия (оригинал цел!)");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 295");
    }

    // A1 · Верни 7/2 целочисленно. Подсказка: int/int → int, дробь отбрасывается.
    static int intDiv() {
        // TODO: ✍️ return 7 / 2;
        throw new UnsupportedOperationException("A1 not implemented");
    }

    // A2 · Верни Integer.MAX_VALUE + 1. Подсказка: переполнение заворачивается по кругу.
    static int overflows() {
        // TODO: ✍️ верни сумму; убедись, что получился MIN_VALUE, и пойми почему
        throw new UnsupportedOperationException("A2 not implemented");
    }

    // A3 · Верни (int)(2.9 / 2). Подсказка: сначала double-деление, потом отброс дроби.
    static int castTrap() {
        // TODO: ✍️ одна строка
        throw new UnsupportedOperationException("A3 not implemented");
    }

    // A4 · Верни -7 % 3. Подсказка: в Java знак остатка = знак делимого.
    static int negMod() {
        // TODO: ✍️ одна строка
        throw new UnsupportedOperationException("A4 not implemented");
    }

    // A5 · При i=5 посчитай i++ + ++i. Подсказка: постфикс сначала отдаёт, потом растит.
    static int incTrap() {
        // TODO: ✍️ сначала ответь ВСЛУХ (постфикс отдаёт старое, префикс растит сразу!), потом напиши код и сверься с 12
        throw new UnsupportedOperationException("A5 not implemented");
    }

    // B1 · Сумма элементов обычным for.
    static int sum(int[] a) {
        // TODO: ✍️ аккумулятор + цикл
        throw new UnsupportedOperationException("B1 not implemented");
    }

    // B2 · Среднее как double. Ловушка: int/int даст int! Дели на (double) длину.
    static double average(int[] a) {
        // TODO: ✍️ используй свой sum() из B1 и поделись на длину. Вышло целое вместо 2.5? Подумай о типах операндов /
        throw new UnsupportedOperationException("B2 not implemented");
    }

    // B3 · Размах: max - min за один проход.
    static int range(int[] a) {
        // TODO: ✍️ tracked max и min, верни разность
        throw new UnsupportedOperationException("B3 not implemented");
    }

    // B4 · Телеметрия в обратном порядке: новый массив-перевёртыш (исходный не трогаем!).
    // Направление: заведи result той же длины; подумай, какой элемент исходного
    // должен встать на позицию i. Подсказка: индекс считается от конца.
    static int[] reversed(int[] a) {
        // TODO: ✍️ допиши сам по направлению выше, в конце не забудь return result;
        throw new UnsupportedOperationException("B4 not implemented");
    }
    static boolean hasDuplicates(int[] a) {
        // TODO: ✍️ вложенные циклы, при совпадении сразу return true
        throw new UnsupportedOperationException("B5 not implemented");
    }

    // B6 · Второй максимум среди РАЗЛИЧНЫХ значений. Подсказка: следи за max1 и max2.
    // ДЛЯ СМЕЛЫХ: что вернуть для {5}? Для пустого? Придумай контракт сам и запиши в комментарий!
    static int secondMax(int[] a) {
        // TODO: ✍️ два трекера; дубликат максимума вторым не считается
        throw new UnsupportedOperationException("B6 not implemented");
    }

    // B7 · Диспетчер ищет ракету по бортовому номеру: верни индекс или -1 (контракт как у indexOf!).
    static int indexOf(int[] a, int value) {
        // TODO: ✍️ иди по массиву и возвращай СРАЗУ при совпадении; что вернуть, если не нашёл, — вспомни контракт indexOf
        throw new UnsupportedOperationException("B7 not implemented");
    }

    // B8 · Сколько раз value встречается в массиве.
    static int frequency(int[] a, int value) {
        // TODO: ✍️ счётчик + if
        throw new UnsupportedOperationException("B8 not implemented");
    }

    // B9 · Склейка: новый массив a + b подряд.
    static int[] merge(int[] a, int[] b) {
        // TODO: ✍️ new int[a.length + b.length], два цикла копирования
        throw new UnsupportedOperationException("B9 not implemented");
    }

    // B10 · Пересечение без дубликатов в результате. Подсказка: contains-проверка + hasDuplicates-идея.
    static int[] intersection(int[] a, int[] b) {
        // TODO: ✍️ собери совпадения, пропусти уже добавленные; верни массив точного размера
        throw new UnsupportedOperationException("B10 not implemented");
    }

    // B11 · Сумма всех элементов двумерного массива. Подсказка: цикл в цикле.
    static int matrixSum(int[][] m) {
        // TODO: ✍️ вложенные циклы по m[i][j]
        throw new UnsupportedOperationException("B11 not implemented");
    }

    // B12 · Отсортированная КОПИЯ: Arrays.copyOf + Arrays.sort. Оригинал не меняем!
    static int[] sortedCopy(int[] a) {
        // TODO: ✍️ два шага в правильном порядке: сначала копия, потом сортировка копии. Перепутаешь — испортишь оригинал!
        throw new UnsupportedOperationException("B12 not implemented");
    }
}
