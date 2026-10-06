// ============================================================
// QUEST 12 · ПИЛОТ · Enum на практике
// Уровни ROADMAP: LVL 18Б, 18В
//
//   ./q 12
//
// Enum — это класс с фиксированным набором объектов: у него бывают поля,
// конструктор, методы и даже поведение у каждой константы.
// Максимум: 140 XP.
// ============================================================

public class Quest12_Enum {

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

    enum Rank {
        RECRUIT("Рядовой"),
        PILOT("Пилот"),
        CAPTAIN("Капитан");

        private final String title;

        Rank(String title) {
            this.title = title;
        }

        String title() {
            return title;
        }
    }

    // Enum с ПОВЕДЕНИЕМ: у каждой ракеты свой расчёт тяги.
    enum Ship {
        FALCON("Falcon") {
            @Override
            int thrust() {
                return 500;
            }
        },
        STARSHIP("Starship") {
            @Override
            int thrust() {
                // TODO 5: ✍️ У Falcon уже 500 (смотри выше). Допиши тягу Starship САМ: 1500.
                // Это и есть поведение константы: у каждой — свой метод!
                throw new UnsupportedOperationException("5 not implemented");
            }
        };

        private final String model;

        Ship(String model) {
            this.model = model;
        }

        String model() {
            return model;
        }

        abstract int thrust();
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 12 · ENUM ===");

        try {
            check(countRanks() == 3, "1 · values() перечисляет всё", 15);
        } catch (UnsupportedOperationException e) {
            todo("1 · values() перечисляет всё");
        }
        try {
            check(byName("PILOT") == Rank.PILOT, "2 · valueOf по имени", 15);
        } catch (UnsupportedOperationException e) {
            todo("2 · valueOf по имени");
        }
        try {
            check(allTitles().equals("Рядовой,Пилот,Капитан"), "3 · собери все title через цикл", 20);
        } catch (UnsupportedOperationException e) {
            todo("3 · собери все title через цикл");
        }
        try {
            check(greet(Rank.PILOT).equals("Привет, Пилот!") && greet(Rank.RECRUIT).equals("Привет, Рядовой!"),
                    "4 · switch по enum", 20);
        } catch (UnsupportedOperationException e) {
            todo("4 · switch по enum");
        }
        try {
            check(totalThrust() == 2000 && Ship.FALCON.model().equals("Falcon"),
                    "5 · полиморфизм констант: сумма тяг", 25);
        } catch (UnsupportedOperationException e) {
            todo("5 · полиморфизм констант: сумма тяг");
        }
        try {
            check(secondRank() == Rank.PILOT, "6 · порядковый номер values()[1]", 15);
        } catch (UnsupportedOperationException e) {
            todo("6 · порядковый номер values()[1]");
        }
        try {
            check(parseRank("CAPTAIN") == Rank.CAPTAIN, "7а · разбор валидного имени", 15);
        } catch (UnsupportedOperationException e) {
            todo("7а · разбор валидного имени");
        }
        try {
            boolean thrown = false;
            try {
                parseRank("ADMIRAL");
            } catch (IllegalArgumentException e) {
                thrown = true;
            }
            check(thrown, "7б · мусор бросает IllegalArgumentException", 15);
        } catch (UnsupportedOperationException e) {
            todo("7б · мусор бросает IllegalArgumentException");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 140");
    }

    // 1 · Сколько званий всего. Подсказка: values() — обычный массив, у него есть length.
    static int countRanks() {
        // TODO 1: ✍️ return Rank.values().length;
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · Разбери имя в Rank. Подсказка: Rank.valueOf(name).
    static Rank byName(String name) {
        // TODO 2: ✍️ одна строка
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · Собери все title через запятую циклом по values().
    static String allTitles() {
        // TODO 3: ✍️ цикл + StringBuilder или join через "+"; ожидание: "Рядовой,Пилот,Капитан"
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 5 · Суммарная тяга флота: полиморфный вызов thrust() у каждой константы.
    static int totalThrust() {
        // TODO 5: ✍️ цикл по Ship.values(), суммируй s.thrust()
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 4 · Приветствие по званию через switch. Подсказка: case PILOT: (без Rank.!).
    static String greet(Rank rank) {
        // TODO 4: ✍️ switch (rank) { case PILOT -> ...; default -> ...; }
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 6 · Второй по объявлению. Подсказка: values() — обычный массив!
    static Rank secondRank() {
        // TODO 6: ✍️ return Rank.values()[1];
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · Разбор имени в Rank. Невалидное пусть бросает исключение само (valueOf умеет!).
    static Rank parseRank(String name) {
        // TODO 7: ✍️ return Rank.valueOf(name);
        throw new UnsupportedOperationException("7 not implemented");
    }
}
