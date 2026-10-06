// ============================================================
// QUEST 28 · ШТУРМАН · Functional interfaces: Predicate/Function/...
// Уровни ROADMAP: LVL 29Б, 29В
//
//   ./q 28
//
// Стандартная четвёрка java.util.function + method references +
// знаменитая ловушка захвата переменной цикла. Свои интерфейсы — тоже.
// Максимум: 140 XP.
// ============================================================

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Quest28_Functional {

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

    // Свой функциональный интерфейс: ровно один абстрактный метод!
    @FunctionalInterface
    interface SkillCheck {
        boolean test(int skill);
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 28 · FUNCTIONAL ===");

        try {
            check(isEven().test(4) && !isEven().test(5), "1 · Predicate", 15);
        } catch (UnsupportedOperationException e) {
            todo("1 · Predicate");
        }
        try {
            check(shoutCompose(), "2 · Function + andThen", 20);
        } catch (UnsupportedOperationException e) {
            todo("2 · Function + andThen");
        }
        try {
            check(collectNames().equals(List.of("А", "Б")), "3 · Consumer собирает", 15);
        } catch (UnsupportedOperationException e) {
            todo("3 · Consumer собирает");
        }
        try {
            check(supplyOr(false).equals("запасной") && supplyOr(true).equals("свой"),
                    "4 · Supplier как запасной", 15);
        } catch (UnsupportedOperationException e) {
            todo("4 · Supplier как запасной");
        }
        try {
            check(lenRef().apply("Союз") == 4, "5 · method reference", 15);
        } catch (UnsupportedOperationException e) {
            todo("5 · method reference");
        }
        try {
            check(captured().equals(List.of(0, 1, 2)), "6 · ловушка захвата цикла", 25);
        } catch (UnsupportedOperationException e) {
            todo("6 · ловушка захвата цикла");
        }
        try {
            check(maxVar().apply(3, 9) == 9, "7 · var в параметрах лямбды", 15);
        } catch (UnsupportedOperationException e) {
            todo("7 · var в параметрах лямбды");
        }
        try {
            SkillCheck check = skillAtLeast(5);
            check(check.test(7) && !check.test(3), "8 · свой интерфейс + лямбда", 20);
        } catch (UnsupportedOperationException e) {
            todo("8 · свой интерфейс + лямбда");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 140");
    }

    // 1 · Чётность. Подсказка: n -> n % 2 == 0.
    static Predicate<Integer> isEven() {
        // TODO 1: ✍️ return n -> n % 2 == 0;
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · Композиция: trim, потом длина. Верни true, если длина "  Союз  " после trim == 4.
    static boolean shoutCompose() {
        // TODO 2: ✍️ скомпонуй обрезку пробелов и длину через andThen. Проверь на строке с пробелами. Что делает andThen?
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · Consumer, копящий имена в список. Верни ["А", "Б"].
    static List<String> collectNames() {
        // TODO 3: ✍️ собери имена через консьюмер. Какой method reference добавляет в список?
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · Есть своё — верни его, нет — возьми у Supplier. Подсказка: Supplier<String> backup.
    static String supplyOr(boolean hasOwn) {
        // TODO 4: ✍️ Supplier<String> backup = () -> "запасной";
        // return hasOwn ? "свой" : backup.get();
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · Длина строкой-ссылкой, не лямбдой. Подсказка: String::length.
    static Function<String, Integer> lenRef() {
        // TODO 5: ✍️ return String::length;
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · ЛОВУШКА: лямбда в цикле захватит... что? Без копии НЕ СКОМПИЛИРУЕТСЯ
    // (переменная должна быть effectively final!). Сделай копию и верни [0, 1, 2].
    static List<Integer> captured() {
        // TODO 6: ✍️ List<Supplier<Integer>> fns = new ArrayList<>();
        // for (int i = 0; i < 3; i++) { int copy = i; fns.add(() -> copy); }
        // List<Integer> out = new ArrayList<>(); for (Supplier<Integer> f : fns) out.add(f.get());
        // return out;
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · Максимум двух через var-параметры: (var a, var b) -> ...
    static java.util.function.BiFunction<Integer, Integer, Integer> maxVar() {
        // TODO 7: ✍️ return (var a, var b) -> a >= b ? a : b;
        throw new UnsupportedOperationException("7 not implemented");
    }

    // 8 · Фабрика проверок "навык >= min" своим интерфейсом.
    static SkillCheck skillAtLeast(int min) {
        // TODO 8: ✍️ return skill -> skill >= min;
        throw new UnsupportedOperationException("8 not implemented");
    }
}
