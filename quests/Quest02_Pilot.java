// ============================================================
// QUEST 02 · ПИЛОТ · ООП: классы, наследование, полиморфизм
// Уровни ROADMAP: LVL 13–19
//
//   javac -encoding UTF-8 quests/Quest02_Pilot.java
//   java -cp quests Quest02_Pilot
//
// Максимум: 130 XP.
// ============================================================

public class Quest02_Pilot {

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
        System.out.println("=== QUEST 02 · ПИЛОТ ===");

        try {
            MiniRocket r = new MiniRocket("Сокол", 200);
            r.refuel();
            check(r.name.equals("Сокол") && r.fuel == 200 && r.isReady(),
                    "LVL-13 · класс, this, конструктор", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-13 · класс, this, конструктор");
        }

        try {
            MiniRocket r = new MiniRocket("  ", 200);
            check(false, "LVL-14 · инкапсуляция (не должно дойти сюда)", 0);
        } catch (IllegalArgumentException e) {
            check(true, "LVL-14 · инкапсуляция: пустое имя запрещено", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-14 · инкапсуляция");
        }

        try {
            MiniCrew pilot = new MiniPilot("Анна", 8);
            MiniCrew engineer = new MiniEngineer("Дмитрий", 7);
            check(pilot.role().equals("PILOT") && engineer.role().equals("ENGINEER"),
                    "LVL-16 · наследование: роли", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-16 · наследование: роли");
        }

        try {
            MiniCrew pilot = new MiniPilot("Анна", 8);
            MiniCrew engineer = new MiniEngineer("Дмитрий", 7);
            check(pilot.work().contains("Анна") && pilot.work().contains("полёт")
                    && engineer.work().contains("Дмитрий") && engineer.work().contains("двигател"),
                    "LVL-16 · overriding: work()", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-16 · overriding: work()");
        }

        try {
            // Полиморфизм: один массив, разные поведения — методы чьи?
            MiniCrew[] crew = {new MiniPilot("Анна", 8), new MiniEngineer("Дмитрий", 7)};
            String report = crewReport(crew);
            check(report.equals("Анна:PILOT|Дмитрий:ENGINEER|"),
                    "LVL-19 · полиморфизм: отчёт по экипажу", 30);
        } catch (UnsupportedOperationException e) {
            todo("LVL-19 · полиморфизм: отчёт по экипажу");
        }

        try {
            MiniPilot ace = new MiniPilot("Ас", 10);
            check(ace.bonus() > 0.05 && ace.bonus() < 0.20, "LVL-15 · бонус пилота от скилла", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-15 · бонус пилота от скилла");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 130");
    }

    // LVL-19 · Собери строку "Имя:РОЛЬ|Имя:РОЛЬ|" обычным циклом.
    // Фишка: у crew[i] виден только тип MiniCrew — а вызовется метод наследника!
    static String crewReport(MiniCrew[] crew) {
        // TODO LVL-19: ✍️ цикл + crew[i].name + ":" + crew[i].role()
        throw new UnsupportedOperationException("LVL-19 not implemented");
    }

    // LVL-13/14 · Мини-ракета. Допиши:
    //   - конструктор (this.name = ..., проверка имени: blank → IllegalArgumentException!)
    //   - refuel() (fuel = capacity)
    //   - isReady() (fuel == capacity)
    static class MiniRocket {
        String name;
        int fuel;
        final int capacity;

        MiniRocket(String name, int capacity) {
            // TODO LVL-13/14: ✍️ this + валидация имени
            throw new UnsupportedOperationException("LVL-13 not implemented");
        }

        void refuel() {
            // TODO LVL-13: ✍️ одна строка
            throw new UnsupportedOperationException("LVL-13 not implemented");
        }

        boolean isReady() {
            // TODO LVL-13: ✍️ одна строка
            throw new UnsupportedOperationException("LVL-13 not implemented");
        }
    }

    // LVL-15/16 · База экипажа. Допиши конструктор через super-вызовы в наследниках,
    // поле skill с проверкой 1..10 (иначе IllegalArgumentException) — это LVL-15.
    static abstract class MiniCrew {
        final String name;
        final int skill;

        MiniCrew(String name, int skill) {
            // TODO LVL-15: ✍️ присвой поля + проверь skill (1..10)
            throw new UnsupportedOperationException("LVL-15 not implemented");
        }

        abstract String role();

        String work() {
            return name + " работает.";
        }
    }

    static class MiniPilot extends MiniCrew {
        MiniPilot(String name, int skill) {
            super(name, skill);
            // TODO LVL-16 (эксперимент!): ✍️ УДАЛИ строку super(...) и попробуй
            // скомпилировать. Прочитай ошибку. Пойми: у базы нет конструктора
            // без аргументов, поэтому Java ТРЕБУЕТ явный super-вызов первым.
            // Потом ВЕРНИ строку обратно.
        }

        @Override
        String role() {
            return "PILOT";
        }

        @Override
        String work() {
            // TODO LVL-16: ✍️ верни "<имя> готовит полёт." (обратись к полю name!)
            throw new UnsupportedOperationException("LVL-16 not implemented");
        }

        // LVL-15 · Бонус пилота растёт со скиллом: 0.03 + skill * 0.006
        double bonus() {
            // TODO LVL-15: ✍️ формула. Подсказка: поле skill уже проверено в базе
            throw new UnsupportedOperationException("LVL-15 not implemented");
        }
    }

    static class MiniEngineer extends MiniCrew {
        MiniEngineer(String name, int skill) {
            super(name, skill);
            // TODO LVL-16 (эксперимент!): ✍️ то же самое: удали super, прочитай
            // ошибку компилятора, верни обратно. Запомни это правило навсегда.
        }

        @Override
        String role() {
            return "ENGINEER";
        }

        @Override
        String work() {
            // TODO LVL-16: ✍️ верни "<имя> чинит двигатели."
            throw new UnsupportedOperationException("LVL-16 not implemented");
        }
    }
}
