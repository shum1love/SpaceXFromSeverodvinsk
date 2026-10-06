// ============================================================
// QUEST 01 · КАДЕТ · Основы: типы, ветвления, циклы, методы
// Уровни ROADMAP: LVL 6–11
//
//   ./q 1
//
// Максимум: 130 XP.
// ============================================================

public class Quest01_Cadet {

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
        System.out.println("=== QUEST 01 · КАДЕТ ===");

        try {
            // Ловушка: tryBoost НЕ должен менять переменную вызывающего!
            int fuel = 50;
            tryBoost(fuel);
            check(fuel == 50, "LVL-6 · примитивы копируются", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-6 · примитивы копируются");
        }

        try {
            check(canLaunch(true, true, 100) && !canLaunch(true, false, 100)
                    && !canLaunch(false, true, 100) && !canLaunch(true, true, 99),
                    "LVL-7 · if/else: допуск к старту", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-7 · if/else: допуск к старту");
        }

        try {
            check(statusAction("READY").equals("IGNITION")
                    && statusAction("NO_FUEL").equals("REFUEL")
                    && statusAction("DESTROYED").equals("SCRAP")
                    && statusAction("???").equals("UNKNOWN"),
                    "LVL-8 · switch по статусу", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-8 · switch по статусу");
        }

        try {
            check(totalSalary(new int[]{1000, 1500, 1200}) == 3700, "LVL-9 · цикл: сумма зарплат", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-9 · цикл: сумма зарплат");
        }

        try {
            check(maxSkill(new int[]{3, 9, 6, 10, 4}) == 10, "LVL-9 · цикл: лучший скилл", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-9 · цикл: лучший скилл");
        }

        try {
            // Перегрузка: оба метода должны существовать и работать.
            MiniTank tank = new MiniTank(200);
            tank.refuel();
            boolean full = tank.fuel == 200;
            MiniTank half = new MiniTank(200);
            half.refuel(50);
            check(full && half.fuel == 50, "LVL-10 · overloading: refuel/refuel(int)", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-10 · overloading: refuel/refuel(int)");
        }

        try {
            check(launchFee(3) == 2400, "LVL-11 · static/final: тариф", 10);
        } catch (UnsupportedOperationException e) {
            todo("LVL-11 · static/final: тариф");
        }

        try {
            check(unbox(null) == -1, "LVL-6В · NPE при распаковке null", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-6В · NPE при распаковке null");
        }

        try {
            check(cached127() && !cached128(), "LVL-6В · кеш Integer: 127 да, 128 нет", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-6В · кеш Integer: 127 да, 128 нет");
        }

        try {
            check(triggerInit().equals("static-parent,static-child,parent,child"),
                    "LVL-11 · порядок инициализации", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-11 · порядок инициализации");
        }

        try {
            check(Hider.describe(new Hider.Child()).equals("child-instance|parent-static"),
                    "LVL-11 · hiding: static по ссылке, instance по объекту", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-11 · hiding: static по ссылке, instance по объекту");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 210");
    }

    // LVL-6В · Распакуй Integer в int, но null роняет NPE! Верни -1 для null (защита!).
    static int unbox(Integer box) {
        // TODO: ✍️ if (box == null) return -1; return box; — распаковка неявная, NPE реальный!
        throw new UnsupportedOperationException("unbox not implemented");
    }

    // LVL-6В · Integer кеширует -128..127: == работает внутри кеша и врёт снаружи.
    // Верни true оба раза? Нет — верни РЕЗУЛЬТАТЫ сравнений: cache127True и cache128False!
    static boolean cached127() {
        // TODO: ✍️ Integer a = 127, b = 127; return a == b; — один объект из кеша!
        throw new UnsupportedOperationException("cached127 not implemented");
    }

    static boolean cached128() {
        // TODO: ✍️ Integer a = 128, b = 128; return a == b; — разные объекты! Всегда equals для значений!
        throw new UnsupportedOperationException("cached128 not implemented");
    }

    // LVL-11 · Порядок инициализации: static-родитель → static-наследник →
    // конструктор родителя → конструктор наследника. Классы ниже дописывать НЕ надо —
    // они готовы! Твоя задача: ПРЕДСКАЗАТЬ строку, потом проверить запуском.
    static String triggerInit() {
        // TODO: ✍️ new InitOrder.C(); return InitOrder.log;
        throw new UnsupportedOperationException("triggerInit not implemented");
    }

    static class InitOrder {
        static String log = "";

        static class P {
            static {
                log += "static-parent,";
            }

            P() {
                log += "parent,";
            }
        }

        static class C extends P {
            static {
                log += "static-child,";
            }

            C() {
                log += "child";
            }
        }
    }

    // LVL-11 · Hiding: static-метод выбирается по типу ССЫЛКИ (compile-time!),
    // instance-метод — по объекту (runtime!). Собери строку "X|Y" сам.
    static class Hider {
        static class Parent {
            static String who() {
                return "parent-static";
            }

            String hello() {
                return "parent-instance";
            }
        }

        static class Child extends Parent {
            static String who() {
                return "child-static";
            }

            @Override
            String hello() {
                return "child-instance";
            }
        }

        static String describe(Parent p) {
            // TODO: ✍️ верни p.who() + "|" + p.hello() для new Child().
            // Предскажи ДО запуска: "child-static|child-instance" или "parent-static|child-instance"?
            throw new UnsupportedOperationException("describe not implemented");
        }
    }

    // LVL-6 · Этот метод НЕ МОЖЕТ изменить fuel вызывающего (примитив!).
    // Допиши тело так, чтобы check прошёл: просто прибавь 100 к параметру.
    // Вывод: пойми, ПОЧЕМУ снаружи ничего не меняется.
    static void tryBoost(int fuel) {
        // TODO LVL-6: ✍️ одна строка: fuel = fuel + 100; (и осознай, что снаружи всё равно 50)
        throw new UnsupportedOperationException("LVL-6 not implemented");
    }

    // LVL-7 · Ракета летит, только если: исправна И бак полон И экипаж на борту.
    static boolean canLaunch(boolean healthy, boolean tankFull, int crew) {
        // TODO LVL-7: ✍️ верни правильное boolean-выражение (подсказка: &&  и crew > 0)
        throw new UnsupportedOperationException("LVL-7 not implemented");
    }

    // LVL-8 · Верни команду по статусу. Используй switch (со стрелками -> как в меню проекта).
    static String statusAction(String status) {
        // TODO LVL-8: ✍️ switch по status: READY→IGNITION, NO_FUEL→REFUEL,
        // DAMAGED→REPAIR, DESTROYED→SCRAP, всё остальное→UNKNOWN
        throw new UnsupportedOperationException("LVL-8 not implemented");
    }

    // LVL-9 · Посчитай сумму обычным for-циклом (стримы будут на LVL-30, терпи!).
    static int totalSalary(int[] salaries) {
        // TODO LVL-9: ✍️ цикл + аккумулятор
        throw new UnsupportedOperationException("LVL-9 not implemented");
    }

    // LVL-9 · Найди максимум. С чего начать: max = salaries[0] или max = 0? Подумай!
    static int maxSkill(int[] skills) {
        // TODO LVL-9: ✍️ цикл с if внутри
        throw new UnsupportedOperationException("LVL-9 not implemented");
    }

    // LVL-11 · Тариф запуска: 800 за единицу сложности. FEE — static final константа.
    static final int FEE = 800;

    static int launchFee(int difficulty) {
        // TODO LVL-11: ✍️ верни FEE * difficulty
        throw new UnsupportedOperationException("LVL-11 not implemented");
    }

    // LVL-10 · Мини-бак. Допиши ОБА метода refuel (перегрузка!):
    //   refuel()     — залить полный бак (fuel = capacity)
    //   refuel(int)  — долить amount, но не больше capacity
    static class MiniTank {
        int fuel = 0;
        final int capacity;

        MiniTank(int capacity) {
            this.capacity = capacity;
        }

        void refuel() {
            // TODO LVL-10: ✍️ одна строка
            throw new UnsupportedOperationException("LVL-10 not implemented");
        }

        void refuel(int amount) {
            // TODO LVL-10: ✍️ долей, но не перелей (подсказка: Math.min)
            throw new UnsupportedOperationException("LVL-10 not implemented");
        }
    }
}
