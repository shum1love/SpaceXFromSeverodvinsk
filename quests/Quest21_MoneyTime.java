// ============================================================
// QUEST 21 · СВЯЗИСТ · BigDecimal и Date/Time: деньги и часы без магии
// Уровни ROADMAP: LVL 37Б, 37В, 38В, 38Г
//
//   ./q 21
//
// Деньги в double — классический production-баг. Даты в long'ах — тоже.
// Учим деньги в BigDecimal и время в java.time.
// Максимум: 160 XP.
// ============================================================

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Quest21_MoneyTime {

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
        System.out.println("=== QUEST 21 · ДЕНЬГИ И ВРЕМЯ ===");

        try {
            check(doubleSum() != 0.3, "1 · 0.1+0.2 в double НЕ 0.3!", 15);
        } catch (UnsupportedOperationException e) {
            todo("1 · 0.1+0.2 в double НЕ 0.3!");
        }
        try {
            check(exactSum(), "2 · BigDecimal считает точно", 20);
        } catch (UnsupportedOperationException e) {
            todo("2 · BigDecimal считает точно");
        }
        try {
            check(divideUp().equals("3.34"), "3 · деление + округление HALF_UP", 20);
        } catch (UnsupportedOperationException e) {
            todo("3 · деление + округление HALF_UP");
        }
        try {
            check(eqVsCmp(), "4 · equals врёт, compareTo — нет", 20);
        } catch (UnsupportedOperationException e) {
            todo("4 · equals врёт, compareTo — нет");
        }
        try {
            check(finalPrice("1000", "10").equals("900.00"), "5 · цена со скидкой", 20);
        } catch (UnsupportedOperationException e) {
            todo("5 · цена со скидкой");
        }
        try {
            check(prettyDate("2026-10-04").equals("4 октября 2026"), "6 · парсинг + русский формат", 20);
        } catch (UnsupportedOperationException e) {
            todo("6 · парсинг + русский формат");
        }
        try {
            check(daysBetween("2026-10-04", "2026-10-14") == 10, "7 · дней между датами", 15);
        } catch (UnsupportedOperationException e) {
            todo("7 · дней между датами");
        }
        try {
            check(arrival("2026-10-04T10:00", 26).equals("2026-10-05T12:00"),
                    "8 · вылет + Duration", 15);
        } catch (UnsupportedOperationException e) {
            todo("8 · вылет + Duration");
        }
        try {
            check(fullMonths("2026-01-15", "2026-10-04") == 8, "9 · Period: полных месяцев", 15);
        } catch (UnsupportedOperationException e) {
            todo("9 · Period: полных месяцев");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 160");
    }

    // 1 · Верни 0.1 + 0.2 как double. Проверка ждёт НЕравенства 0.3 — вот она, двоичная дробь!
    static double doubleSum() {
        // TODO 1: ✍️ return 0.1 + 0.2;
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · Верни true, если 0.1 + 0.2 в BigDecimal ТОЧНО равно 0.3 (через compareTo!).
    static boolean exactSum() {
        // TODO 2: ✍️ BigDecimal sum = new BigDecimal("0.1").add(new BigDecimal("0.2"));
        // return sum.compareTo(new BigDecimal("0.3")) == 0;
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 3 · 10.02 / 3 с округлением до 2 знаков HALF_UP → "3.34". Без scale будет исключение!
    static String divideUp() {
        // TODO 3: ✍️ подели с округлением до 2 знаков HALF_UP. Без scale взорвётся — почему?
        throw new UnsupportedOperationException("3 not implemented");
    }

    // 4 · Верни true, если equals("2.0","2.00")==false, а compareTo==0. Оба факта сразу!
    static boolean eqVsCmp() {
        // TODO 4: ✍️ сравни 2.0 и 2.00 обоими способами. Результаты разные? Объясни вслух!
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · Цена минус скидка%: строки на входе, "900.00" на выходе (scale 2!).
    static String finalPrice(String price, String discountPct) {
        // TODO 5: ✍️ скидка 10% → множитель 0.9. Как получить из процентов? (movePointLeft!) scale в конце!
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · "2026-10-04" → "4 октября 2026". Подсказка: parse + format с паттерном и локалью!
    static String prettyDate(String iso) {
        // TODO 6: ✍️ распарси ISO, отформатируй паттерном с русской локалью. Два вызова!
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · Полных дней между датами ISO. Подсказка: ChronoUnit.DAYS.between.
    static long daysBetween(String from, String to) {
        // TODO 7: ✍️ return ChronoUnit.DAYS.between(LocalDate.parse(from), LocalDate.parse(to));
        throw new UnsupportedOperationException("7 not implemented");
    }

    // 8 · Вылет ISO + N часов полёта → прилёт ISO строкой. Подсказка: plusHours + toString.
    static String arrival(String departureIso, int hours) {
        // TODO 8: ✍️ return LocalDateTime.parse(departureIso).plusHours(hours).toString();
        throw new UnsupportedOperationException("8 not implemented");
    }

    // 9 · Полных месяцев между датами. Подсказка: Period.between(...).toTotalMonths().
    // ZonedDateTime/ZoneId/Duration/Period импортированы не зря — потрогай их в jshell/отладчике!
    static long fullMonths(String from, String to) {
        // TODO 9: ✍️ календарная разница между датами + месяцы итогом. Какой класс? (не Duration!)
        throw new UnsupportedOperationException("9 not implemented");
    }

    // Бонус без XP: ZonedDateTime.now(ZoneId.of("Europe/Moscow")) — время Байконура.
    // Duration.ofHours(26) — длительность полёта. Это уже используется в MissionService!
    static String baikonurNow() {
        return ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toString();
    }
}
