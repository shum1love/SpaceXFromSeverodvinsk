// ============================================================
// QUEST 06 · КАПИТАН · Тесты: лови баги как AQA-инженер
// Уровни ROADMAP: LVL 42–44
//
// Фишка этого квеста: код НИЖЕ СОДЕРЖИТ БАГИ (как в реальной жизни!).
// Твоя работа: сначала допиши ПРОВЕРКИ, которые ловят баг (тест красный),
// потом ИСПРАВЬ код (тест зелёный). Именно так работает AQA/SDET.
//
//   ./q 6
//
// Максимум: 120 XP.
// ============================================================

public class Quest06_Captain {

    static int xp = 0;

    static void check(boolean condition, String name, int reward) {
        if (condition) {
            xp += reward;
            System.out.println("✅ " + name + " (+" + reward + " XP)");
        } else {
            System.out.println("❌ " + name + " — неверный результат (или баг ещё жив)");
        }
    }

    static void todo(String name) {
        System.out.println("⬜ " + name + " — ещё не сделано");
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 06 · КАПИТАН ===");
        System.out.println("Легенда: стажёр написал MiniBilling. Говорят, там 3 бага.");
        System.out.println("Твои тесты ниже должны их поймать. Удачи, капитан.");
        System.out.println();

        try {
            // Граница: скилл 10 + тренировка → должен остаться 10, а не 11!
            check(capSkill(10) == 10 && capSkill(7) == 8, "LVL-42 · граница скилла", 30);
        } catch (UnsupportedOperationException e) {
            todo("LVL-42 · граница скилла");
        }

        try {
            // Ноль и отрицательные деньги:take-off запрещён! Должно бросать исключение.
            boolean zeroThrows = false;
            boolean negativeThrows = false;
            try {
                MiniBilling.charge(0);
            } catch (IllegalArgumentException e) {
                zeroThrows = true;
            }
            try {
                MiniBilling.charge(-500);
            } catch (IllegalArgumentException e) {
                negativeThrows = true;
            }
            MiniBilling.charge(500); // а это — легально, не должно бросать
            check(zeroThrows && negativeThrows, "LVL-43 · невалидные суммы отклонены", 40);
        } catch (UnsupportedOperationException e) {
            todo("LVL-43 · невалидные суммы отклонены");
        }

        try {
            // Скидка применяется ТОЛЬКО к положительным счетам, иначе баг с минусом!
            // charge(-100) уже запрещён сверху, но discount(-50, 10) — что вернёт?
            check(MiniBilling.discount(1000, 10) == 900 && MiniBilling.discount(1000, 0) == 1000,
                    "LVL-44 · скидка: нормальные случаи", 20);
        } catch (UnsupportedOperationException e) {
            todo("LVL-44 · скидка: нормальные случаи");
        }

        try {
            // Параметризованный прогон: все границы скидки 0/10/50/100%.
            int[][] cases = {{1000, 0, 1000}, {1000, 10, 900}, {1000, 50, 500}, {1000, 100, 0}};
            boolean allOk = true;
            for (int[] c : cases) {
                if (MiniBilling.discount(c[0], c[1]) != c[2]) {
                    allOk = false;
                }
            }
            check(allOk, "LVL-44 · скидка: все границы (параметризация)", 30);
        } catch (UnsupportedOperationException e) {
            todo("LVL-44 · скидка: все границы");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 120");
        if (xp == 120) {
            System.out.println("Баги повержены. Стажёр уволен (шутка). Ты — настоящий SDET.");
        }
    }

    // LVL-42 · Тренировка: +1 к скиллу, но ПОТОЛОК — 10.
    // ВНИМАНИЕ: реализация ниже специально сломана (бага №1). Найди и исправь!
    static int capSkill(int skill) {
        // TODO LVL-42: ✍️ сначала запусти — увидишь ❌. Потом исправь метод (подсказка: Math.min).
        return skill + 1;
    }

    // Биллинг стажёра. Баги №2 и №3 где-то здесь. Тесты выше тебе всё расскажут.
    static class MiniBilling {

        // Списать сумму со счёта. Ноль и минус — запрещены (баг №2: проверки нет!).
        static void charge(int amount) {
            // TODO LVL-43: ✍️ если amount <= 0 — брось IllegalArgumentException
            // (сейчас метод молча всё пропускает — тест это ловит)
            if (amount == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("impossible");
            }
        }

        // Применить скидку percent% к счёту. Баг №3: формула перепутана!
        static int discount(int amount, int percent) {
            // TODO LVL-44: ✍️ запусти — границы 0% и 100% провалятся. Правильно: amount * (100 - percent) / 100
            return amount * percent / 100;
        }
    }
}
