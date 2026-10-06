// ============================================================
// QUEST 11 · ПИЛОТ · final и настоящая неизменяемость
// Уровни ROADMAP: LVL 15Б
//
//   ./q 11
//
// Главная ловушка файла: final запрещает менять СТРЕЛКУ, но не ДОМ.
// Неизменяемый объект приходится строить специально: финал-поля +
// защитные копии на входе и выходе. Строим RocketSpec с нуля.
// Максимум: 150 XP.
// ============================================================

import java.util.ArrayList;
import java.util.List;

public class Quest11_Immutable {

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
        System.out.println("=== QUEST 11 · IMMUTABLE ===");

        try {
            Box b = new Box(5);
            touchFinal(b);
            check(b.value == 6, "1 · final-параметр: объект менять МОЖНО", 20);
        } catch (UnsupportedOperationException e) {
            todo("1 · final-параметр: объект менять МОЖНО");
        }
        try {
            List<String> crew = new ArrayList<>(List.of("А"));
            grow(crew);
            check(crew.size() == 2, "2 · final-список: добавлять МОЖНО", 20);
        } catch (UnsupportedOperationException e) {
            todo("2 · final-список: добавлять МОЖНО");
        }
        try {
            RocketSpec spec = new RocketSpec("Сокол", 100, List.of("А", "Б"));
            check(spec.name().equals("Сокол") && spec.thrust() == 100 && spec.crew().size() == 2,
                    "3 · неизменяемая спека: конструктор + геттеры", 20);
        } catch (UnsupportedOperationException e) {
            todo("3 · неизменяемая спека: конструктор + геттеры");
        }
        try {
            List<String> outer = new ArrayList<>(List.of("А"));
            RocketSpec spec = new RocketSpec("С", 10, outer);
            outer.add("Хакер");
            check(spec.crew().size() == 1, "4 · защита на ВХОДЕ (копия!)", 25);
        } catch (UnsupportedOperationException e) {
            todo("4 · защита на ВХОДЕ (копия!)");
        }
        try {
            RocketSpec spec = new RocketSpec("С", 10, List.of("А"));
            spec.crew().add("Хакер");
            check(spec.crew().size() == 1, "5 · защита на ВЫХОДЕ (копия!)", 20);
        } catch (UnsupportedOperationException e) {
            todo("5 · защита на ВЫХОДЕ (копия!)");
        }
        try {
            RocketSpec a = new RocketSpec("С", 10, List.of("А"));
            RocketSpec b = new RocketSpec("С", 10, List.of("А"));
            check(a.equals(b) && a.hashCode() == b.hashCode(), "6 · equals по содержимому", 20);
        } catch (UnsupportedOperationException e) {
            todo("6 · equals по содержимому");
        }
        try {
            RocketSpec spec = new RocketSpec("С", 10, List.of("А"));
            RocketSpec boosted = spec.withThrust(50);
            check(boosted.thrust() == 50 && spec.thrust() == 10, "7 · with-метод: новый объект!", 25);
        } catch (UnsupportedOperationException e) {
            todo("7 · with-метод: новый объект!");
        }

        try {
            check(new LateInit(42).code() == 42, "8 · blank final: присвоение в конструкторе", 20);
        } catch (UnsupportedOperationException e) {
            todo("8 · blank final: присвоение в конструкторе");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 170");
    }

    // 8 · Blank final: поле final БЕЗ инициализатора — присвоить можно РОВНО ОДИН раз,
    // и конструктор для этого подходит! Допиши класс: поле + присваивание + геттер.
    static class LateInit {
        final int code;

        LateInit(int code) {
            // TODO 8: ✍️ this.code = code;
            throw new UnsupportedOperationException("8 not implemented");
        }

        int code() {
            // TODO 8: ✍️ return code;
            throw new UnsupportedOperationException("8 not implemented");
        }
    }

    static class Box {
        int value;

        Box(int value) {
            this.value = value;
        }
    }

    // 1 · final у параметра запрещает только ПЕРЕНАЗНАЧЕНИЕ. Поле менять можно!
    static void touchFinal(final Box b) {
        // TODO 1: ✍️ b.value++;
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · final у списка тоже держит только стрелку. add работает!
    static void grow(final List<String> crew) {
        // TODO 2: ✍️ crew.add("Б");
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3-7 · Неизменяемая характеристика ракеты. Правила класса:
    // все поля private final, сеттеров НЕТ, списки копируются на входе и выходе.
    static final class RocketSpec {
        private final String name;
        private final int thrust;
        private final List<String> crew;

        RocketSpec(String name, int thrust, List<String> crew) {
            // TODO 3: ✍️ присвой поля. А список — подумай: можно ли хранить ЧУЖОЙ?
            // Задача 4 покарает, если да! (Подсказка: ArrayList — растущий список,
            // new ArrayList<>(чужой) делает независимую копию. Подробно — LVL 23.)
            throw new UnsupportedOperationException("3 not implemented");
        }

        String name() {
            // TODO 3: ✍️ верни поле (геттеры вместо getName — стиль record)
            throw new UnsupportedOperationException("3 not implemented");
        }

        int thrust() {
            // TODO 3: ✍️ верни поле
            throw new UnsupportedOperationException("3 not implemented");
        }

        List<String> crew() {
            // TODO 5: ✍️ верни НЕ поле, а ... что? Задача 5-тест допишет "Хакера" снаружи — проверь!
            throw new UnsupportedOperationException("5 not implemented");
        }

        @Override
        public boolean equals(Object o) {
            // TODO 6: ✍️ паттерн из LVL-22: сравни все три поля. Хэш — через Objects (import добавь сам!)
            throw new UnsupportedOperationException("6 not implemented");
        }

        @Override
        public int hashCode() {
            // TODO 6: ✍️ Objects.hash(name, thrust, crew) — понадобится import java.util.Objects!
            throw new UnsupportedOperationException("6 not implemented");
        }

        // 7 · "Изменение" неизменяемого = новый объект с одним другим полем.
        RocketSpec withThrust(int newThrust) {
            // TODO 7: ✍️ return new RocketSpec(name, newThrust, crew);
            throw new UnsupportedOperationException("7 not implemented");
        }
    }
}
