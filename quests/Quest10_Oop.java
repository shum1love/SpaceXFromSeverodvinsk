// ============================================================
// QUEST 10 · ПИЛОТ · ООП вглубь: диспетчеризация, композиция, ловушки
// Уровни ROADMAP: LVL 16В, 16Г, 19Б, 20Б
//
//   ./q 10
//
// Здесь живут три знаменитые ловушки собеседований: overload-vs-override,
// "конструктор родителя — всегда первым" и композиция против наследования.
// Максимум: 190 XP.
// ============================================================

import java.util.ArrayList;
import java.util.List;

public class Quest10_Oop {

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

    // --- Иерархия для задач 1-4 ---
    static abstract class Crew {
        final String name;

        Crew(String name) {
            this.name = name;
        }

        abstract String role();

        // Шаблонный метод: общий алгоритм + абстрактный шаг.
        String introduce() {
            return name + ":" + role();
        }
    }

    static class TestPilot extends Crew {
        TestPilot(String name) {
            super(name);
        }

        @Override
        String role() {
            // TODO 1: ✍️ верни "PILOT"
            throw new UnsupportedOperationException("1 not implemented");
        }
    }

    // --- Ловушка overload-vs-override (задачи 3-4) ---
    static class Speaker {
        String speak(Object o) {
            return "object";
        }
    }

    static class LoudSpeaker extends Speaker {
        String speak(String s) {
            return "string";
        }
    }

    // --- Композиция (задача 5): у ракеты ЕСТЬ двигатель, а не "ракета — двигатель" ---
    static class Engine {
        int thrust;

        Engine(int thrust) {
            this.thrust = thrust;
        }
    }

    static class Rocket {
        final Engine engine;

        Rocket(int thrust) {
            // TODO 5а: ✍️ this.engine = new Engine(thrust);
            throw new UnsupportedOperationException("5a not implemented");
        }

        int thrust() {
            // TODO 5б: ✍️ делегируй двигателю: return engine.thrust;
            throw new UnsupportedOperationException("5b not implemented");
        }
    }

    // --- Порядок конструкторов (задача 6) ---
    static final List<String> BUILD_LOG = new ArrayList<>();

    static class Base {
        Base() {
            BUILD_LOG.add("base");
        }
    }

    static class Child extends Base {
        Child() {
            // TODO 6: ✍️ ничего писать не надо — super() вставится сам!
            // Просто убедись, что лог будет [base, child]. Удали throw и оставь пустое тело.
            throw new UnsupportedOperationException("6 not implemented");
        }
    }

    // --- Два интерфейса сразу (задача 9) ---
    interface Flyable {
        default String fly() {
            return "лечу";
        }
    }

    interface Repairable {
        default String fix() {
            return "чиню";
        }
    }

    static class Drone implements Flyable, Repairable {
        // TODO 9: ✍️ верни fly() + "+" + fix()
        String combo() {
            // Внимание: fly() и fix() уже работают (default-методы) — задача 2 отдельно докажет это.
            throw new UnsupportedOperationException("9 not implemented");
        }
    }

    // Второй наследник для задачи 7: полностью готовый, писать тут нечего.
    static class Engineer2 extends Crew {
        Engineer2(String name) {
            super(name);
        }

        @Override
        String role() {
            return "ENGINEER";
        }
    }

    // 10 · Верни ПРОСТОЕ имя реального класса объекта (не ссылки!).
    // Подсказка: o.getClass().getSimpleName(). Для LoudSpeaker → "LoudSpeaker".
    static String runtimeType(Object o) {
        // TODO 10: ✍️ одна строка
        throw new UnsupportedOperationException("10 not implemented");
    }

    // 2 · Вызови default-метод дважды и склей. Писать почти нечего — в этом и смысл default!
    static String flyTwice(Drone drone) {
        // TODO 2: ✍️ return drone.fly() + drone.fly();
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · Ссылка типа Speaker, объект LoudSpeaker. Чей speak(String) выберет компилятор?
    static String whichSpeaks(Speaker s) {
        // TODO 3: ✍️ return s.speak("привет"); — вернётся "object", докажи запуском!
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · А здесь ссылка LoudSpeaker — выберется speak(String). Тот же вызов, другой результат!
    static String whichSpeaksLoud(LoudSpeaker s) {
        // TODO 4: ✍️ return s.speak("привет");
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 7 · True только для пилотов. Подсказка: return c instanceof TestPilot;
    static boolean isPilot(Crew c) {
        // TODO 7: ✍️ одна строка
        throw new UnsupportedOperationException("7 not implemented");
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 10 · ООП ВГЛУБЬ ===");

        try {
            check(new TestPilot("Анна").introduce().equals("Анна:PILOT"),
                    "1 · abstract + шаблонный метод", 20);
        } catch (UnsupportedOperationException e) {
            todo("1 · abstract + шаблонный метод");
        }
        try {
            check(flyTwice(new Drone()).equals("лечулечу"), "2 · default-метод интерфейса даром", 15);
        } catch (UnsupportedOperationException e) {
            todo("2 · default-метод интерфейса даром");
        }
        try {
            Speaker s = new LoudSpeaker();
            // ВНИМАНИЕ, ловушка: выбор перегрузки — по типу ССЫЛКИ (Speaker) на этапе компиляции!
            check(whichSpeaks(s).equals("object"), "3 · overload биндится по ссылке!", 25);
        } catch (UnsupportedOperationException e) {
            todo("3 · overload биндится по ссылке!");
        }
        try {
            LoudSpeaker loud = new LoudSpeaker();
            check(whichSpeaksLoud(loud).equals("string"), "4 · а по LoudSpeaker — другая перегрузка", 15);
        } catch (UnsupportedOperationException e) {
            todo("4 · а по LoudSpeaker — другая перегрузка");
        }
        try {
            check(new Rocket(100).thrust() == 100, "5 · композиция: делегирование", 20);
        } catch (UnsupportedOperationException e) {
            todo("5 · композиция: делегирование");
        }
        try {
            BUILD_LOG.clear();
            new Child();
            check(BUILD_LOG.equals(List.of("base", "child")), "6 · родитель конструируется первым", 20);
            // List.of(...) выше — короткая фабрика неизменяемого списка (подробно — LVL 26Б).
        } catch (UnsupportedOperationException e) {
            todo("6 · родитель конструируется первым");
        }
        try {
            check(isPilot(new TestPilot("Б")) && !isPilot(new Engineer2("В")),
                    "7 · instanceof различает наследников", 20);
        } catch (UnsupportedOperationException e) {
            todo("7 · instanceof различает наследников");
        }
        try {
            Crew[] team = {new TestPilot("А"), new TestPilot("Б")};
            StringBuilder sb = new StringBuilder();
            for (Crew c : team) {
                sb.append(c.introduce()).append(";");
            }
            check(sb.toString().equals("А:PILOT;Б:PILOT;"), "8 · полиморфный массив", 15);
        } catch (UnsupportedOperationException e) {
            todo("8 · полиморфный массив");
        }
        try {
            check(new Drone().combo().equals("лечу+чиню"), "9 · два интерфейса сразу", 20);
        } catch (UnsupportedOperationException e) {
            todo("9 · два интерфейса сразу");
        }
        try {
            check(runtimeType(new LoudSpeaker()).equals("LoudSpeaker")
                    && runtimeType("строка").equals("String"),
                    "10 · getClass() видит РЕАЛЬНЫЙ тип", 20);
        } catch (UnsupportedOperationException e) {
            todo("10 · getClass() видит РЕАЛЬНЫЙ тип");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 190");
    }
}
