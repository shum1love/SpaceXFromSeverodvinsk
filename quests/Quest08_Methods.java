// ============================================================
// QUEST 08 · КАДЕТ · Методы, область видимости, передача аргументов
// Уровни ROADMAP: LVL 7Б, 10Б, 10В, 10Г
//
//   ./q 8
//
// Главная тема файла — pass-by-value: в Java ВСЕГДА копируется значение,
// просто для объектов копируется ссылка. Три задачки 6-8 докажут это делом.
// Максимум: 175 XP.
// ============================================================

public class Quest08_Methods {

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
        System.out.println("=== QUEST 08 · МЕТОДЫ ===");

        try {
            check(sumPositive(new int[]{-1, 2, -3, 4}) == 6, "1 · сумма положительных (scope!)", 15);
        } catch (UnsupportedOperationException e) {
            todo("1 · сумма положительных (scope!)");
        }
        try {
            check(sign(-5).equals("минус") && sign(0).equals("ноль") && sign(7).equals("плюс"),
                    "2 · тернарник: знак числа", 15);
        } catch (UnsupportedOperationException e) {
            todo("2 · тернарник: знак числа");
        }
        try {
            check(alarm(true, false, true) && !alarm(false, false, true) && !alarm(true, true, true),
                    "3 · логика: (temp && !door) || night", 15);
        } catch (UnsupportedOperationException e) {
            todo("3 · логика: (temp && !door) || night");
        }
        try {
            check(firstPositive(new int[]{-2, -1, 5, 9}) == 5 && firstPositive(new int[]{-1}) == -1,
                    "4 · early return: первый положительный", 20);
        } catch (UnsupportedOperationException e) {
            todo("4 · early return: первый положительный");
        }
        try {
            check(describe(42).equals("число 42") && describe("Союз").equals("строка Союз"),
                    "5 · перегрузка describe", 20);
        } catch (UnsupportedOperationException e) {
            todo("5 · перегрузка describe");
        }
        try {
            int x = 10;
            tryDouble(x);
            check(x == 10, "6 · примитив снаружи не меняется", 15);
        } catch (UnsupportedOperationException e) {
            todo("6 · примитив снаружи не меняется");
        }
        try {
            int[] box = {10};
            bump(box);
            check(box[0] == 11, "7 · содержимое объекта МЕНЯЕТСЯ через копию ссылки", 20);
        } catch (UnsupportedOperationException e) {
            todo("7 · содержимое объекта МЕНЯЕТСЯ через копию ссылки");
        }
        try {
            int[] arr = {1, 2};
            reassign(arr);
            check(arr.length == 2 && arr[0] == 1, "8 · подмена ссылки НЕ видна снаружи", 20);
        } catch (UnsupportedOperationException e) {
            todo("8 · подмена ссылки НЕ видна снаружи");
        }
        try {
            String a = "Земля", b = "Марс";
            trySwap(a, b);
            check(a.equals("Земля") && b.equals("Марс"), "9 · ловушка: swap строк невозможен", 20);
        } catch (UnsupportedOperationException e) {
            todo("9 · ловушка: swap строк невозможен");
        }
        try {
            check(maxOfThree(3, 9, 7) == 9, "10 · максимум из трёх", 15);
        } catch (UnsupportedOperationException e) {
            todo("10 · максимум из трёх");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 175");
    }

    // 1 · Сумма только положительных. Ловушка scope: счётчик объяви ДО цикла!
    static int sumPositive(int[] a) {
        // TODO: ✍️ int total = 0; цикл с if (a[i] > 0); return total;
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · Верни "минус"/"ноль"/"плюс" одним тернарником (можно вложенным).
    static String sign(int n) {
        // TODO: ✍️ return n < 0 ? "минус" : (n == 0 ? "ноль" : "плюс");
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · Сигнализация: (температура норм И дверь закрыта) ИЛИ ночь.
    // temp=true значит "норма", door=true значит "открыта".
    static boolean alarm(boolean temp, boolean door, boolean night) {
        // TODO: ✍️ return (temp && !door) || night; — пойми каждый оператор
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · Первый положительный или -1. Без флага: нашёл → сразу return.
    static int firstPositive(int[] a) {
        // TODO: ✍️ найди первый положительный и верни СРАЗУ; не нашёл — вспомни контракт indexOf
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · Два метода-тёзки: для int верни "число N", для String — "строка S".
    static String describe(int n) {
        // TODO: ✍️ return "число " + n;
        throw new UnsupportedOperationException("5a not implemented");
    }

    static String describe(String s) {
        // TODO: ✍️ return "строка " + s;
        throw new UnsupportedOperationException("5b not implemented");
    }

    // 6 · Этот метод НЕ МОЖЕТ изменить x вызывающего: внутрь прилетела копия.
    static void tryDouble(int x) {
        // TODO: ✍️ x = x * 2; — и осознай, что снаружи всё равно 10
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · А здесь содержимое МЕНЯЕТСЯ: копия ссылки ведёт в тот же массив.
    static void bump(int[] box) {
        // TODO: ✍️ box[0]++;
        throw new UnsupportedOperationException("7 not implemented");
    }

    // 8 · А подмена самой ссылки снаружи НЕ ВИДНА: меняем копию стрелки.
    static void reassign(int[] arr) {
        // TODO: ✍️ arr = new int[]{9, 9, 9}; — снаружи массив прежний, проверь!
        throw new UnsupportedOperationException("8 not implemented");
    }

    // 9 · Поменять две строки местами через метод НЕЛЬЗЯ: String immutable
    // плюс копия ссылки. Напиши тело и убедись, что снаружи ничего не меняется.
    static void trySwap(String a, String b) {
        // TODO: ✍️ String t = a; a = b; b = t; — снаружи "Земля" и "Марс" на месте
        throw new UnsupportedOperationException("9 not implemented");
    }

    // 10 · Максимум из трёх любым способом (Math.max разрешён!).
    static int maxOfThree(int a, int b, int c) {
        // TODO: ✍️ максимум из трёх (класс Math поможет). Одной строкой — слабо?
        throw new UnsupportedOperationException("10 not implemented");
    }
}
