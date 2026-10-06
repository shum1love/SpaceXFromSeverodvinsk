// ============================================================
// QUEST 23 · КАПИТАН · Аннотации и рефлексия: откуда магия JUnit
// Уровни ROADMAP: LVL 45В, 45Г
//
//   ./q 23
//
// JUnit находит твои тесты рефлексией: читает методы с @Test.
// Здесь делаем то же самое руками: своя аннотация + Class/Method/Field.
// Максимум: 130 XP.
// ============================================================

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Quest23_Reflect {

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

    // Своя аннотация-метка для важных миссий. RUNTIME — иначе рефлексия её не увидит!
    @Retention(RetentionPolicy.RUNTIME)
    @interface Critical {
        String value() default "";
    }

    static class FlightPlan {
        private String code = "RK-1";

        @Critical("стыковка")
        public void dock() {
        }

        public void cruise() {
        }

        @Deprecated
        public void oldRoute() {
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== QUEST 23 · REFLECT ===");

        try {
            check(isCritical("dock") && !isCritical("cruise"), "1 · есть ли аннотация", 20);
        } catch (UnsupportedOperationException e) {
            todo("1 · есть ли аннотация");
        }
        try {
            check(criticalValue("dock").equals("стыковка"), "2 · значение аннотации", 20);
        } catch (UnsupportedOperationException e) {
            todo("2 · значение аннотации");
        }
        try {
            check(criticalMethods().equals(List.of("dock")), "3 · все помеченные методы", 25);
        } catch (UnsupportedOperationException e) {
            todo("3 · все помеченные методы");
        }
        try {
            check(readCode().equals("RK-1"), "4 · чтение private-поля", 25);
        } catch (UnsupportedOperationException e) {
            todo("4 · чтение private-поля");
        }
        try {
            check(invokeDock().equals("ok"), "5 · вызов метода рефлексией", 20);
        } catch (UnsupportedOperationException e) {
            todo("5 · вызов метода рефлексией");
        }
        try {
            check(isDeprecated("oldRoute"), "6 · @Deprecated видно тоже", 20);
        } catch (UnsupportedOperationException e) {
            todo("6 · @Deprecated видно тоже");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 130");
    }

    // 1 · Есть ли @Critical на методе methodName. Подсказка: getMethod + isAnnotationPresent.
    static boolean isCritical(String methodName) throws Exception {
        // TODO 1: ✍️ return FlightPlan.class.getMethod(methodName).isAnnotationPresent(Critical.class);
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · Прочитай value аннотации. Подсказка: getAnnotation(Critical.class).value().
    static String criticalValue(String methodName) throws Exception {
        // TODO 2: ✍️ Method m = FlightPlan.class.getMethod(methodName);
        // return m.getAnnotation(Critical.class).value();
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · Имена ВСЕХ методов с @Critical. Подсказка: getDeclaredMethods + фильтр в цикле.
    static List<String> criticalMethods() {
        // TODO 3: ✍️ List<String> out = new ArrayList<>(); for (Method m : FlightPlan.class.getDeclaredMethods())
        // if (m.isAnnotationPresent(Critical.class)) out.add(m.getName()); return out;
        // Понадобится import java.lang.reflect.Method — он уже есть!
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · Прочитай private-поле code. Подсказка: getDeclaredField + setAccessible(true) + get.
    static String readCode() throws Exception {
        // TODO 4: ✍️ поле приватное: возьми DeclaredField и попроси доступ (setAccessible!). Каст не забудь!
        // Вопрос на засыпку (ответь ДО запуска!): что бросится, если поле вдруг станет int,
        // а каст останется (String)? Правильно: ClassCastException — компилятор тут слеп!
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · Вызови dock() рефлексией и верни "ok". Подсказка: getMethod("dock").invoke(new FlightPlan()).
    static String invokeDock() throws Exception {
        // TODO 5: ✍️ возьми метод и вызови на новом объекте. Верни "ok" после вызова!
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · Помечен ли метод как @Deprecated. Подсказка: как задача 1, класс аннотации — Deprecated.class.
    static boolean isDeprecated(String methodName) throws Exception {
        // TODO 6: ✍️ одна строка по образцу задачи 1
        throw new UnsupportedOperationException("6 not implemented");
    }
}
