// ============================================================
// QUEST 03 · ИНЖЕНЕР · equals, коллекции, generics
// Уровни ROADMAP: LVL 21–26
//
//   javac -encoding UTF-8 quests/Quest03_Engineer.java
//   java -cp quests Quest03_Engineer
//
// Максимум: 160 XP.
// ============================================================

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Quest03_Engineer {

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
        System.out.println("=== QUEST 03 · ИНЖЕНЕР ===");

        try {
            // Ловушка LVL-21: два разных объекта с одинаковым текстом.
            String a = new String("Союз");
            String b = new String("Союз");
            check((a == b) == false && a.equals(b), "LVL-21 · == vs equals (ловушка)", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-21");
        }

        try {
            MiniId first = new MiniId("AAA", "Сокол");
            MiniId second = new MiniId("AAA", "Другое имя");
            MiniId other = new MiniId("BBB", "Сокол");
            Set<MiniId> set = new HashSet<>();
            set.add(first);
            set.add(second);
            set.add(other);
            // Равенство ТОЛЬКО по id → в сете должно остаться 2 элемента!
            check(set.size() == 2 && first.equals(second) && first.hashCode() == second.hashCode(),
                    "LVL-22 · equals/hashCode по id", 30);
        } catch (UnsupportedOperationException e) {
            todo("LVL-22 · equals/hashCode по id");
        }

        try {
            List<Integer> fuel = new ArrayList<>(List.of(100, 200, 150));
            check(sumList(fuel) == 450, "LVL-23 · List: сумма", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-23 · List: сумма");
        }

        try {
            List<String> crew = new ArrayList<>(List.of("Анна", "Дмитрий", "Анна", "Борис", "Дмитрий"));
            check(deduplicate(crew).size() == 3, "LVL-24 · Set: убрать дубликаты", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-24 · Set: убрать дубликаты");
        }

        try {
            // Сгруппируй миссии по статусу ВРУЧНУЮ (без стримов!): {"READY":2, "FAILED":1}
            List<String[]> missions = List.of(
                    new String[]{"A", "READY"}, new String[]{"B", "READY"}, new String[]{"C", "FAILED"});
            Map<String, Integer> grouped = groupByStatus(missions);
            check(grouped.get("READY") == 2 && grouped.get("FAILED") == 1 && grouped.size() == 2,
                    "LVL-25 · Map: группировка циклом", 30);
        } catch (UnsupportedOperationException e) {
            todo("LVL-25 · Map: группировка циклом");
        }

        try {
            Box<String> box = new Box<>();
            box.put("Звезда");
            check(box.get().equals("Звезда"), "LVL-26 · generics: Box<T>", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-26 · generics: Box<T>");
        }

        try {
            List<MiniPilot2> pilots = new ArrayList<>(List.of(
                    new MiniPilot2("Слабый", 2), new MiniPilot2("Сильный", 9)));
            pilots.sort(Comparator.comparingInt(p -> p.skill));
            check(pilots.get(0).name.equals("Слабый")
                    && strongest(pilots).name.equals("Сильный"),
                    "LVL-26 · comparator + generic-метод", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-26 · comparator + generic-метод");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 160");
    }

    // LVL-22 · Равенство ТОЛЬКО по id: допиши equals и hashCode.
    // Контракт: equal ⇒ одинаковый hashCode. Иначе HashSet сломается!
    static class MiniId {
        final String id;
        final String name;

        MiniId(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            // TODO LVL-22: ✍️ this==o → true; !(o instanceof MiniId) → false; сравни id
            throw new UnsupportedOperationException("LVL-22 not implemented");
        }

        @Override
        public int hashCode() {
            // TODO LVL-22: ✍️ верни id.hashCode()
            throw new UnsupportedOperationException("LVL-22 not implemented");
        }
    }

    // LVL-23 · Сумма элементов списка обычным циклом.
    static int sumList(List<Integer> numbers) {
        // TODO LVL-23: ✍️ for по numbers, аккумулятор
        throw new UnsupportedOperationException("LVL-23 not implemented");
    }

    // LVL-24 · Верни список без дубликатов (порядок сохрани!).
    // Подсказка: LinkedHashSet уберёт дубликаты и сохранит порядок. Какой import нужен?
    static List<String> deduplicate(List<String> names) {
        // TODO LVL-24: ✍️ new ArrayList<>(new LinkedHashSet<>(names)) — и нужный import!
        throw new UnsupportedOperationException("LVL-24 not implemented");
    }

    // LVL-25 · Вход: пары [имя, статус]. Выход: статус → количество.
    // Только HashMap + цикл + getOrDefault. Стримы запрещены уставом корабля!
    static Map<String, Integer> groupByStatus(List<String[]> missions) {
        // TODO LVL-25: ✍️ HashMap, цикл, map.put(status, map.getOrDefault(status, 0) + 1)
        throw new UnsupportedOperationException("LVL-25 not implemented");
    }

    // LVL-26 · Обобщённая коробка: один класс — для любого типа.
    static class Box<T> {
        private T value;

        void put(T value) {
            // TODO LVL-26: ✍️ this.value = value
            throw new UnsupportedOperationException("LVL-26 not implemented");
        }

        T get() {
            // TODO LVL-26: ✍️ верни value
            throw new UnsupportedOperationException("LVL-26 not implemented");
        }
    }

    static class MiniPilot2 {
        final String name;
        final int skill;

        MiniPilot2(String name, int skill) {
            this.name = name;
            this.skill = skill;
        }
    }

    // LVL-26 · Обобщённый метод: верни пилота с максимальным скиллом.
    // Подсказка: цикл + сравнение .skill. Работает для ЛЮБОГО списка MiniPilot2.
    static MiniPilot2 strongest(List<MiniPilot2> pilots) {
        // TODO LVL-26: ✍️ цикл, запомни лучшего
        throw new UnsupportedOperationException("LVL-26 not implemented");
    }
}
