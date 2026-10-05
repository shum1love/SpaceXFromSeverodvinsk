package org.example.spacecompany.util;

import java.math.BigDecimal;

/**
 * Центральные крутилки экономики игры.
 *
 * <p>Все поля — {@code public static final}: константы, общие для всех классов
 * без создания объектов. Одно из немногих легитимных применений глобального
 * состояния — настоящие константы, которые никогда не меняются в рантайме.
 */
public final class GameConfig {

    private GameConfig() {
    }

    /** Стартовые деньги новой компании. */
    public static final BigDecimal STARTING_BALANCE = new BigDecimal("20000");

    /** Цена единицы ракетного топлива. */
    public static final BigDecimal FUEL_UNIT_PRICE = new BigDecimal("2");

    /** Цена ремонта за единицу состояния (шкала 0..100). */
    public static final BigDecimal REPAIR_PRICE_PER_POINT = new BigDecimal("15");

    /** Плата за вывод ракеты с планового обслуживания. */
    public static final BigDecimal MAINTENANCE_FEE = new BigDecimal("300");

    /** Шанс (0..1), что проваленная миссия уничтожит ракету сразу. */
    public static final double DESTROY_ON_FAILURE_CHANCE = 0.20;

    /** Верхняя граница случайных событий за полёт. */
    public static final int MAX_EVENTS_PER_FLIGHT = 2;

    /** Имя файла сохранения по умолчанию в рабочей папке. */
    public static final String DEFAULT_SAVE_FILE = "space-company-save.txt";
}
