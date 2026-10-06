// ============================================================
// QUEST 17 · ИНЖЕНЕР · Comparable и Comparator: порядок по-взрослому
// Уровни ROADMAP: LVL 27Б, 27В
//
//   ./q 17
//
// Natural ordering (Comparable, один на класс) против внешних правил
// (Comparator — сколько угодно). Плюс цепочки thenComparing и null'ы.
// Максимум: 130 XP.
// ============================================================

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Quest17_Sort {

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

    // Пилот с ЕСТЕСТВЕННЫМ порядком: сильнее навык — "меньше" (первее).
    static class Pilot implements Comparable<Pilot> {
        final String name;
        final int skill;

        Pilot(String name, int skill) {
            this.name = name;
            this.skill = skill;
        }

        @Override
        public int compareTo(Pilot other) {
            // TODO 1: ✍️ return Integer.compare(other.skill, this.skill); — убывание навыка!
            throw new UnsupportedOperationException("1 not implemented");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 17 · СОРТИРОВКА ===");

        try {
            List<Pilot> team = new ArrayList<>(List.of(new Pilot("А", 3), new Pilot("Б", 9)));
            Collections.sort(team);
            check(team.get(0).name.equals("Б"), "1 · Comparable: естественный порядок", 20);
        } catch (UnsupportedOperationException e) {
            todo("1 · Comparable: естественный порядок");
        }
        try {
            check(byName().compare(new Pilot("Б", 1), new Pilot("А", 9)) > 0,
                    "2 · Comparator-лямбда по имени", 20);
        } catch (UnsupportedOperationException e) {
            todo("2 · Comparator-лямбда по имени");
        }
        try {
            List<Integer> nums = new ArrayList<>(List.of(1, 2, 3));
            nums.sort(desc());
            check(nums.equals(List.of(3, 2, 1)), "3 · reversed()", 15);
        } catch (UnsupportedOperationException e) {
            todo("3 · reversed()");
        }
        try {
            List<Pilot> team = new ArrayList<>(List.of(
                    new Pilot("А", 5), new Pilot("Б", 5), new Pilot("В", 9)));
            team.sort(skillThenName());
            check(team.get(0).name.equals("В") && team.get(1).name.equals("А")
                    && team.get(2).name.equals("Б"), "4 · thenComparing: навык, потом имя", 25);
        } catch (UnsupportedOperationException e) {
            todo("4 · thenComparing: навык, потом имя");
        }
        try {
            List<String> names = new ArrayList<>();
            names.add(null);
            names.add("Б");
            names.add(null);
            names.add("А");
            names.sort(nullsFirst());
            check(names.get(0) == null && names.get(1) == null && names.get(2).equals("А"),
                    "5 · nullsFirst: null'ы не роняют сорт", 25);
        } catch (UnsupportedOperationException e) {
            todo("5 · nullsFirst: null'ы не роняют сорт");
        }
        try {
            // Стабильность: равные сохраняют исходный порядок. A и B с навыком 5.
            List<Pilot> team = new ArrayList<>(List.of(new Pilot("A", 5), new Pilot("B", 5)));
            team.sort(onlySkill());
            check(team.get(0).name.equals("A"), "6 · стабильность сортировки", 25);
        } catch (UnsupportedOperationException e) {
            todo("6 · стабильность сортировки");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 130");
    }

    // 2 · Компаратор по имени лямбдой. Подсказка: (a, b) -> a.name.compareTo(b.name).
    static Comparator<Pilot> byName() {
        // TODO 2: ✍️ return (a, b) -> a.name.compareTo(b.name);
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · Убывание целых. Подсказка: Comparator.<Integer>naturalOrder().reversed().
    static Comparator<Integer> desc() {
        // TODO 3: ✍️ одна строка (нужен дженерик-ядро naturalOrder()!)
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · Сначала навык по убыванию, при равенстве — имя по возрастанию.
    static Comparator<Pilot> skillThenName() {
        // TODO 4: ✍️ сначала навык по убыванию, при равенстве — имя. Собери цепочку сам:
        // какой метод клеит второй критерий? А как перевернуть только первый?
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · Строки, но null'ы — в начало и без NPE.
    static Comparator<String> nullsFirst() {
        // TODO 5: ✍️ строки, null'ы — в начало, без NPE. Готовый обёртка-компаратор существует — найди его сам!
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · ТОЛЬКО навык по убыванию (имена не трогаем — стабильность их сохранит).
    static Comparator<Pilot> onlySkill() {
        // TODO 6: ✍️ ТОЛЬКО навык по убыванию (имена вообще не трогай — стабильность сохранит порядок).
        // Тот же приём, что в задаче 4!
        throw new UnsupportedOperationException("6 not implemented");
    }
}
