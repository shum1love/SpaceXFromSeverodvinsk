// ============================================================
// QUEST 25 · КАПИТАН · Охота на баги: код СЛОМАН, чини сам
// Уровни ROADMAP: LVL 44Г
//
//   ./q 25
//
// Фишка как в Quest06: методы ниже НАПИСАНЫ, но в каждом сидит живой баг.
// Проверки красные, пока не починишь. Падающие с исключением баги обёрнуты
// в try/catch — читай текст ❌, там написано, что чинить.
// Максимум: 130 XP.
// ============================================================

public class Quest25_BugHunt {

    static int xp = 0;

    static void check(boolean condition, String name, int reward) {
        if (condition) {
            xp += reward;
            System.out.println("✅ " + name + " (+" + reward + " XP)");
        } else {
            System.out.println("❌ " + name + " — " + "баг ещё жив");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 25 · ОХОТА НА БАГИ ===");
        System.out.println("Легенда: те же стажёры, новые баги. Чиню всё сам!");

        try {
            check(sumFirst(3, new int[]{1, 2, 3}) == 6, "Баг 1 · граница цикла", 25);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("❌ Баг 1 · ArrayIndexOutOfBounds — чини условие цикла (<= vs <)!");
        }
        check(isReady(new String("READY")), "Баг 2 · сравнение строк", 20);
        check(half(5) == 2.5, "Баг 3 · целочисленное деление", 20);
        try {
            check(countRockets(null) == 0, "Баг 4 · null на входе", 25);
        } catch (NullPointerException e) {
            System.out.println("❌ Баг 4 · NullPointerException — где защита от null?");
        }
        check(max(new int[]{-5, -1, -9}) == -1, "Баг 5 · стартовое значение max", 20);
        check(!isAdultAndPilot(20, false) && isAdultAndPilot(20, true),
                "Баг 6 · копипаста в условии", 20);

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 130");
    }

    // Баг 1 · Сумма ПЕРВЫХ n элементов. Падает с AIOOBE — условие кривое!
    static int sumFirst(int n, int[] a) {
        int sum = 0;
        for (int i = 0; i <= n; i++) { // БАГ: <= вместо <
            sum += a[i];
        }
        return sum;
    }

    // Баг 2 · Готов ли статус? == сравнивает ССЫЛКИ, а не текст!
    static boolean isReady(String status) {
        return status == "READY"; // БАГ: нужно equals
    }

    // Баг 3 · Половина дистанции. 5/2 в int'ах — это 2, а не 2.5!
    static double half(int dist) {
        return dist / 2; // БАГ: целочисленное деление
    }

    // Баг 4 · Сколько ракет в списке. А если списка нет (null)?
    static int countRockets(java.util.List<String> rockets) {
        return rockets.size(); // БАГ: нет защиты от null
    }

    // Баг 5 · Максимум. На {-5,-1,-9} вернёт 0 — такого числа даже нет!
    static int max(int[] a) {
        int best = 0; // БАГ: старт должен быть a[0]
        for (int x : a) {
            if (x > best) {
                best = x;
            }
        }
        return best;
    }

    // Баг 6 · Взрослый пилот? Второе условие скопировано с первого!
    static boolean isAdultAndPilot(int age, boolean pilot) {
        return age >= 18 && age >= 18; // БАГ: второе должно быть pilot
    }
}
