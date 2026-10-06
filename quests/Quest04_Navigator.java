// ============================================================
// QUEST 04 · ШТУРМАН · Лямбды, стримы, Optional, исключения
// Уровни ROADMAP: LVL 29–34
//
//   ./q 4
//
// Максимум: 170 XP.
// ============================================================

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Quest04_Navigator {

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
        System.out.println("=== QUEST 04 · ШТУРМАН ===");

        try {
            List<Integer> skills = new ArrayList<>(List.of(3, 9, 6, 10, 4));
            sortDesc(skills);
            check(skills.equals(List.of(10, 9, 6, 4, 3)), "LVL-29 · лямбда: сортировка", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-29 · лямбда: сортировка");
        }

        try {
            // Оставь зарплаты > 1000, подними на 10%, посчитай сумму.
            // 1200→1320, 800→мимо, 1500→1650. Итого 2970.
            check(raiseAndSum(List.of(1200, 800, 1500)) == 2970, "LVL-30 · стрим: filter+map+sum", 30);
        } catch (UnsupportedOperationException e) {
            todo("LVL-30 · стрим: filter+map+sum");
        }

        try {
            List<String> names = new ArrayList<>(List.of("Анна", "Дмитрий", "Борис"));
            check(longNames(names).equals(List.of("ДМИТРИЙ")),
                    "LVL-31 · method ref + стрим", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-31 · method ref + стрим");
        }

        try {
            check(findPilot(List.of("инженер", "пилот", "учёный")).orElse("никого").equals("пилот")
                    && findPilot(List.of("инженер")).isEmpty(),
                    "LVL-32 · Optional вместо null", 30);
        } catch (UnsupportedOperationException e) {
            todo("LVL-32 · Optional вместо null");
        }

        try {
            launchOrThrow(50);
            check(false, "LVL-33 · исключение (не должно дойти сюда)", 0);
        } catch (NoFuelException e) {
            check(e.getMessage().contains("топлива"), "LVL-33 · своё исключение", 30);
        } catch (UnsupportedOperationException e) {
            todo("LVL-33 · своё исключение");
        }

        try {
            // finally обязан освободить пилота, даже если полёт рухнул!
            MiniPilot3 pilot = new MiniPilot3("Анна");
            try {
                flyWithCleanup(pilot, true);
            } catch (RuntimeException expected) {
                // так и задумано: полёт падает
            }
            check(pilot.busy == false, "LVL-34 · finally освобождает ресурсы", 40);
        } catch (UnsupportedOperationException e) {
            todo("LVL-34 · finally освобождает ресурсы");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 170");
    }

    // LVL-29 · Отсортируй по убыванию ЛЯМБДОЙ: (a, b) -> ...
    // Подсказка: для убывания сравни b с a: Integer.compare(b, a) или b - a.
    static void sortDesc(List<Integer> skills) {
        // TODO LVL-29: ✍️ skills.sort((a, b) -> ...);
        throw new UnsupportedOperationException("LVL-29 not implemented");
    }

    // LVL-30 · Конвейер: filter(> 1000) → map(+10%) → sum.
    // Целочисленно: s * 110 / 100. Терминальная операция — sum() у IntStream (mapToInt!).
    static int raiseAndSum(List<Integer> salaries) {
        // TODO LVL-30: ✍️ return salaries.stream().filter(...).mapToInt(...).sum();
        throw new UnsupportedOperationException("LVL-30 not implemented");
    }

    // LVL-31 · Оставь имена длиннее 5 символов, верни их В ВЕРХНЕМ РЕГИСТРЕ.
    // Требование: для регистра используй method reference String::toUpperCase.
    static List<String> longNames(List<String> names) {
        // TODO LVL-31: ✍️ .filter(n -> n.length() > 5).map(String::toUpperCase).toList()
        throw new UnsupportedOperationException("LVL-31 not implemented");
    }

    // LVL-32 · Найди "пилот" в списке. Есть → Optional.of, нет → Optional.empty().
    // НИКАКИХ null! Подсказка: stream().filter(...).findFirst()
    static Optional<String> findPilot(List<String> roles) {
        // TODO LVL-32: ✍️ одна строка со стримом
        throw new UnsupportedOperationException("LVL-32 not implemented");
    }

    // LVL-33 · Своё исключение: если fuel < 100 — брось NoFuelException с текстом про топливо.
    static void launchOrThrow(int fuel) {
        // TODO LVL-33: ✍️ if (fuel < 100) throw new NoFuelException("...");
        throw new UnsupportedOperationException("LVL-33 not implemented");
    }

    static class NoFuelException extends RuntimeException {
        NoFuelException(String message) {
            super(message);
        }
    }

    static class MiniPilot3 {
        final String name;
        boolean busy = true;

        MiniPilot3(String name) {
            this.name = name;
        }
    }

    // LVL-34 · Полёт: если explode == true — урони полёт исключением,
    // НО пилота (busy = false) освободи в finally в любом случае!
    static void flyWithCleanup(MiniPilot3 pilot, boolean explode) {
        // TODO LVL-34: ✍️ try { if (explode) throw new RuntimeException("boom"); } finally { ... }
        throw new UnsupportedOperationException("LVL-34 not implemented");
    }
}
