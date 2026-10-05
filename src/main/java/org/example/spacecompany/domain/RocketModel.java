package org.example.spacecompany.domain;

import java.math.BigDecimal;

/**
 * Каталог моделей ракет для покупки.
 *
 * <p>Наследник самого первого enum'а проекта {@code Rockets}
 * ({@code FALCON_ONE}, {@code FALCON_NINE}, {@code FALCON_HEAVY}, {@code STARSHIP}).
 * Старый enum хранил только имя и цену; этот сохранил их и добавил характеристики
 * для симулятора (топливо, груз, стоимость запуска, надёжность).
 */
public enum RocketModel {

    FALCON_1("Falcon 1", "3000", 100, 500, "500", 0.85),
    FALCON_9("Falcon 9", "5000", 200, 2_000, "800", 0.90),
    FALCON_HEAVY("Falcon Heavy", "6000", 300, 5_000, "1200", 0.88),
    STARSHIP("Starship", "9000", 500, 10_000, "1500", 0.82);

    private final String displayName;
    private final BigDecimal price;
    private final int maxFuel;
    private final int payloadCapacityKg;
    private final BigDecimal launchCost;
    /** Базовая надёжность в диапазоне (0, 1). В полёте режется плохим состоянием. */
    private final double baseReliability;

    RocketModel(String displayName,
                String price,
                int maxFuel,
                int payloadCapacityKg,
                String launchCost,
                double baseReliability) {
        this.displayName = displayName;
        this.price = new BigDecimal(price);
        this.maxFuel = maxFuel;
        this.payloadCapacityKg = payloadCapacityKg;
        this.launchCost = new BigDecimal(launchCost);
        this.baseReliability = baseReliability;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getMaxFuel() {
        return maxFuel;
    }

    public int getPayloadCapacityKg() {
        return payloadCapacityKg;
    }

    public BigDecimal getLaunchCost() {
        return launchCost;
    }

    public double getBaseReliability() {
        return baseReliability;
    }

    @Override
    public String toString() {
        return displayName + " (цена=" + price
                + ", топливо=" + maxFuel
                + ", груз=" + payloadCapacityKg + " кг"
                + ", запуск=" + launchCost
                + ", надёжность=" + baseReliability + ")";
    }
}
