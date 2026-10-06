// ============================================================
// QUEST 09 · КАДЕТ · Память: ссылки, объекты, null, жизнь объекта
// Уровни ROADMAP: LVL 11В
//
//   ./q 9
//
// Здесь почти нет "напиши алгоритм" — здесь "предскажи и докажи".
// Каждый метод — мини-вопрос про кучу, ссылки и null. Сначала ответь
// вслух, потом напиши код и сверься с проверкой.
// Максимум: 135 XP.
// ============================================================

public class Quest09_Memory {

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

    static class Box {
        int value;

        Box(int value) {
            this.value = value;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 09 · ПАМЯТЬ ===");

        try {
            check(sharedMutation() == 99, "1 · две ссылки — один объект", 15);
        } catch (UnsupportedOperationException e) {
            todo("1 · две ссылки — один объект");
        }
        try {
            check(reassignIsolation() == 1, "2 · подмена одной ссылки", 15);
        } catch (UnsupportedOperationException e) {
            todo("2 · подмена одной ссылки");
        }
        try {
            check(nullCutsLink() == 5, "3 · null рвёт только свою стрелку", 15);
        } catch (UnsupportedOperationException e) {
            todo("3 · null рвёт только свою стрелку");
        }
        try {
            check(stringFork().equals("A"), "4 · строки: += рождает НОВЫЙ объект", 20);
        } catch (UnsupportedOperationException e) {
            todo("4 · строки: += рождает НОВЫЙ объект");
        }
        try {
            check(arrayAlias() == 9, "5 · массивы — тоже объекты", 15);
        } catch (UnsupportedOperationException e) {
            todo("5 · массивы — тоже объекты");
        }
        try {
            check(escapeByReturn().value == 7, "6 · return спасает объект из метода", 20);
        } catch (UnsupportedOperationException e) {
            todo("6 · return спасает объект из метода");
        }
        try {
            check(nextTicket() == 1 && nextTicket() == 2, "7 · поле накапливает, локалка — нет", 15);
        } catch (UnsupportedOperationException e) {
            todo("7 · поле накапливает, локалка — нет");
        }
        try {
            check(safeLength(null) == -1 && safeLength("Союз") == 4, "8 · защита от null (мастхэв AQA)", 20);
        } catch (UnsupportedOperationException e) {
            todo("8 · защита от null (мастхэв AQA)");
        }

        try {
            check(depthGuard(1000).equals("caught"), "9 · StackOverflowError ловится!", 20);
        } catch (UnsupportedOperationException e) {
            todo("9 · StackOverflowError ловится!");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 155");
    }

    // 9 · Бесконечная рекурсия роняет стек — но SOE это Error, его МОЖНО поймать!
    // (В проде так не делают: чинят рекурсию. Здесь — чтобы увидеть ошибку глазами.)
    static String depthGuard(int depth) {
        // TODO 9: ✍️ try { dive(depth); return "survived"; }
        // catch (StackOverflowError e) { return "caught"; }
        // Плюс метод: static void dive(int d) { dive(d + 1); } — напиши его рядом!
        throw new UnsupportedOperationException("9 not implemented");
    }

    // 1 · p и q указывают на ОДИН Box. Поменяй через p, прочитай через q. Что увидишь?
    static int sharedMutation() {
        // TODO: ✍️ Box p = new Box(1); Box q = p; p.value = 99; return q.value;
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · q перенаправили на новый Box(2). Что теперь лежит в p.value? Верни его.
    static int reassignIsolation() {
        // TODO: ✍️ Box p = new Box(1); Box q = p; q = new Box(2); return p.value;
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · p = null. Объект умер? Нет — q его держит! Верни q.value (там 5).
    static int nullCutsLink() {
        // TODO: ✍️ Box p = new Box(5); Box q = p; p = null; return q.value;
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · s2 = s1, потом s1 += "B". Что в s2? Подсказка: строки immutable!
    static String stringFork() {
        // TODO: ✍️ String s1 = "A"; String s2 = s1; s1 += "B"; return s2;
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · Массив — объект: b = a, b[0] = 9. Что теперь a[0]? Верни его.
    static int arrayAlias() {
        // TODO: ✍️ int[] a = {1}; int[] b = a; b[0] = 9; return a[0];
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · Объект, созданный внутри метода, обычно умирает вместе с кадром.
    // Но если метод ВЕРНУЛ ссылку — объект живёт дальше! Верни new Box(7).
    static Box escapeByReturn() {
        // TODO: ✍️ return new Box(7); — снаружи .value == 7, объект спасён
        throw new UnsupportedOperationException("6 not implemented");
    }

    static int ticketState = 0;

    // 7 · Счётчик талонов: поле помнит значение между вызовами, локалка — нет.
    // Верни ++ticketState. Проверка вызовет дважды и ждёт 1, потом 2.
    static int nextTicket() {
        // TODO: ✍️ return ++ticketState;
        throw new UnsupportedOperationException("7 not implemented");
    }

    // 8 · Безопасная длина: null → -1, иначе s.length(). Классика AQA-кода.
    static int safeLength(String s) {
        // TODO: ✍️ if (s == null) return -1; return s.length();
        throw new UnsupportedOperationException("8 not implemented");
    }
}
