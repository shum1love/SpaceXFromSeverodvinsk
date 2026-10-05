package org.example.spacecompany.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Маленькие статические помощники для денег.
 *
 * <p>Финальный класс с приватным конструктором и только статическими методами —
 * стандартная форма утилитного класса (как {@code java.util.Collections}).
 */
public final class MoneyUtils {

    /** Локаль для денег: рубли, копейки через запятую. */
    private static final Locale RU = new Locale("ru", "RU");

    private MoneyUtils() {
    }

    /** Форматирует сумму в рублях, например {@code 12 345,67 ₽}. */
    public static String format(BigDecimal amount) {
        if (amount == null) {
            return NumberFormat.getCurrencyInstance(RU).format(BigDecimal.ZERO);
        }
        NumberFormat format = NumberFormat.getCurrencyInstance(RU);
        return format.format(amount);
    }

    /** Форматирует вероятность (0..1) в проценты, например {@code 73,5%}. */
    public static String formatPercent(double probability) {
        return "%.1f%%".formatted(probability * 100.0);
    }

    /** Парсит ввод вроде "1_000", "1000.5" в рубли с копейками. */
    public static BigDecimal parse(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Текст суммы не должен быть null");
        }
        String cleaned = text.strip().replace("_", "").replace(",", ".").replace(" ", "");
        return new BigDecimal(cleaned).setScale(2, RoundingMode.HALF_EVEN);
    }
}
