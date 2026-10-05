package org.example.spacecompany.domain.mission;

import java.math.BigDecimal;

/**
 * Справочник видов миссий. Каждый вид задаёт сложность по умолчанию,
 * награду, потребность в грузоподъёмности и минимальный экипаж.
 */
public enum MissionType {

    SATELLITE_LAUNCH("Запуск спутника", 3, "3000", 500, 1,
            "Вывести спутник на орбиту."),
    CARGO_DELIVERY("Доставка груза", 4, "4500", 2_000, 2,
            "Доставить груз на орбитальную станцию."),
    RESEARCH("Научная миссия", 5, "6000", 1_000, 2,
            "Провести научные эксперименты на орбите."),
    MOON_MISSION("Лунная миссия", 7, "12000", 3_000, 3,
            "Высадить экипаж на Луну и благополучно вернуть."),
    MARS_MISSION("Марсианская миссия", 9, "25000", 5_000, 4,
            "Отправить пилотируемую экспедицию на Марс. Самый сложный контракт.");

    private final String displayName;
    /** Сложность по шкале 1..10; чем выше, тем ниже шанс успеха. */
    private final int difficulty;
    private final BigDecimal baseReward;
    private final int requiredPayloadKg;
    private final int minCrewSize;
    private final String description;

    MissionType(String displayName, int difficulty, String baseReward,
                int requiredPayloadKg, int minCrewSize, String description) {
        this.displayName = displayName;
        this.difficulty = difficulty;
        this.baseReward = new BigDecimal(baseReward);
        this.requiredPayloadKg = requiredPayloadKg;
        this.minCrewSize = minCrewSize;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public BigDecimal getBaseReward() {
        return baseReward;
    }

    public int getRequiredPayloadKg() {
        return requiredPayloadKg;
    }

    public int getMinCrewSize() {
        return minCrewSize;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName + " (сложность=" + difficulty
                + ", награда=" + baseReward
                + ", груз=" + requiredPayloadKg + " кг"
                + ", экипаж>=" + minCrewSize + ")";
    }
}
