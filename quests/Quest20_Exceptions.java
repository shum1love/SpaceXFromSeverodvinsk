// ============================================================
// QUEST 20 · ШТУРМАН · Исключения вглубь: свои, ловушки, ресурсы
// Уровни ROADMAP: LVL 33Б, 33В, 34Б
//
//   ./q 20
//
// Три знаменитые ловушки return+finally (их ОБОЖАЮТ на собеседованиях),
// свои исключения, multi-catch и порядок закрытия ресурсов.
// Максимум: 205 XP.
// ============================================================

import java.util.ArrayList;
import java.util.List;

public class Quest20_Exceptions {

    static int xp = 0;
    static final List<String> LOG = new ArrayList<>();

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

    // Своё ПРОВЕРЯЕМОЕ исключение: компилятор заставит объявить throws.
    static class NoFuelChecked extends Exception {
        NoFuelChecked(String message) {
            super(message);
        }
    }

    // Ресурс-болтун: пишет в LOG, когда его открывают/закрывают.
    static class LoudResource implements AutoCloseable {
        private final String name;

        LoudResource(String name) {
            this.name = name;
            LOG.add("open " + name);
        }

        @Override
        public void close() {
            LOG.add("close " + name);
        }
    }

    // Ресурс-вредитель: close бросает исключение.
    static class EvilResource implements AutoCloseable {
        @Override
        public void close() {
            throw new IllegalStateException("close error");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 20 · EXCEPTIONS ===");

        try {
            try {
                launchChecked(50);
                check(false, "1 · своё checked (не должно дойти)", 0);
            } catch (NoFuelChecked e) {
                check(e.getMessage().contains("топлива"), "1 · своё checked + throws", 20);
            }
        } catch (UnsupportedOperationException e) {
            todo("1 · своё checked + throws");
        }
        try {
            check(propagate().equals("поймано в a"), "2 · всплытие a→b→c", 20);
        } catch (UnsupportedOperationException e) {
            todo("2 · всплытие a→b→c");
        }
        try {
            check(parseLen("123") == 3 && parseLen("12x") == -1 && parseLen(null) == -1,
                    "3 · multi-catch", 20);
        } catch (UnsupportedOperationException e) {
            todo("3 · multi-catch");
        }
        try {
            check(trap1() == 2, "4 · ловушка: return в finally бьёт return в try", 25);
        } catch (UnsupportedOperationException e) {
            todo("4 · ловушка: return в finally бьёт return в try");
        }
        try {
            check(trap2() == 1, "5 · ловушка: return-значение фиксируется ДО finally", 25);
        } catch (UnsupportedOperationException e) {
            todo("5 · ловушка: return-значение фиксируется ДО finally");
        }
        try {
            check(trap3().equals("полёт-авария"), "6 · ловушка: мутабельное видно из finally", 25);
        } catch (UnsupportedOperationException e) {
            todo("6 · ловушка: мутабельное видно из finally");
        }
        try {
            LOG.clear();
            resourceOrder();
            check(LOG.equals(List.of("open 1", "open 2", "close 2", "close 1")),
                    "7 · ресурсы закрываются в ОБРАТНОМ порядке", 25);
        } catch (UnsupportedOperationException e) {
            todo("7 · ресурсы закрываются в ОБРАТНОМ порядке");
        }
        try {
            try {
                explode();
                check(false, "8 · тело vs close (не должно дойти)", 0);
            } catch (IllegalStateException e) {
                check(e.getMessage().equals("body"), "8 · побеждает исключение ТЕЛА", 25);
            }
        } catch (UnsupportedOperationException e) {
            todo("8 · побеждает исключение ТЕЛА");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 205");
    }

    // 1 · Топлива < 100 → брось NoFuelChecked. Сигнатура ОБЯЗАНА содержать throws!
    static void launchChecked(int fuel) throws NoFuelChecked {
        // TODO 1: ✍️ if (fuel < 100) throw new NoFuelChecked("мало топлива: " + fuel);
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · Три метода: c бросает, b просто пробрасывает (throws!), a ловит и возвращает "поймано в a".
    static String propagate() {
        // TODO 2: ✍️ try { levelB(); } catch (IllegalStateException e) { return "поймано в a"; } return "?";
        // Плюс методы: static void levelB() { levelC(); }  static void levelC() { throw new ...("бум"); }
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · Длина числа в строке: мусор и null → -1. Один catch на два типа!
    static int parseLen(String s) {
        // TODO 3: ✍️ распарси и верни длину; мусор и null → -1. Один catch на оба типа — как?
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · Что вернётся? try{return 1;} finally{return 2;} → 2! Напиши и убедись.
    static int trap1() {
        // TODO 4: ✍️ ровно этот код с двумя return
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · А здесь? int x = 1; try{return x;} finally{x = 2;} → вернётся 1!
    static int trap2() {
        // TODO 5: ✍️ ровно этот код; пойми: значение зафиксировано ДО finally
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · StringBuilder: append "полёт" в try, верни sb.toString(); в finally допиши "-авария".
    // Вернётся "полёт-авария": объект мутабельный, ссылка та же!
    static String trap3() {
        // TODO 6: ✍️ StringBuilder sb = new StringBuilder();
        // try { sb.append("полёт"); return sb.toString(); } finally { sb.append("-авария"); }
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · Два LoudResource в ОДНОМ try: открой "1", потом "2". Закрытие — в обратном!
    static void resourceOrder() {
        // TODO 7: ✍️ try (LoudResource r1 = new LoudResource("1");
        //              LoudResource r2 = new LoudResource("2")) { }
        throw new UnsupportedOperationException("7 not implemented");
    }

    // 8 · Тело бросает "body", close — "close error". Наружу выйдет "body"
    // (ошибка close станет suppressed-прицепом). Напиши и проверь!
    static void explode() {
        // TODO 8: ✍️ try (EvilResource r = new EvilResource()) { throw new IllegalStateException("body"); }
        throw new UnsupportedOperationException("8 not implemented");
    }
}
