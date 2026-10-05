package org.example.spacecompany.service;

import java.util.List;
import java.util.Objects;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionType;

/**
 * Чистая, детерминированная математика шанса успеха.
 *
 * <p>Специально отдельно от броска кубика: при тех же миссии, ракете и экипаже
 * метод всегда возвращает то же число — поэтому легко тестируется.
 * {@code MissionService} считает вероятность этим классом, а <i>потом</i>
 * бросает {@code Random} относительно неё.
 *
 * <pre>
 *   p = 0.35
 *       + надёжностьРакеты * 0.40
 *       + среднийНавык / 10 * 0.25
 *       + min(среднийОпыт * 0.01, 0.10)
 *       − сложность * 0.05
 *       + сумма(бонусов), с обрезкой в [0.05, 0.98]
 * </pre>
 */
public final class ProbabilityCalculator {

    /** Floor so even a hopeless flight can theoretically succeed. */
    public static final double MIN_PROBABILITY = 0.05;
    /** Ceiling so even a perfect flight can theoretically fail. */
    public static final double MAX_PROBABILITY = 0.98;

    private ProbabilityCalculator() {
    }

    /** Calculates the success probability for a mission + rocket + crew. */
    public static double calculateSuccessProbability(Mission mission, Rocket rocket,
                                                     List<? extends Employee> crew) {
        Objects.requireNonNull(mission, "mission");
        Objects.requireNonNull(rocket, "rocket");
        Objects.requireNonNull(crew, "crew");
        return calculate(mission.getType().getDifficulty(), mission.getRequiredPayloadKg(),
                rocket, crew);
    }

    /** Overloaded variant working directly from a {@link MissionType}. */
    public static double calculateSuccessProbability(MissionType type, Rocket rocket,
                                                     List<? extends Employee> crew) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rocket, "rocket");
        Objects.requireNonNull(crew, "crew");
        return calculate(type.getDifficulty(), type.getRequiredPayloadKg(), rocket, crew);
    }

    private static double calculate(int difficulty, int requiredPayloadKg,
                                    Rocket rocket, List<? extends Employee> crew) {
        double rocketComponent = rocket.getReliability() * 0.40;

        double skillAvg = 0.0;
        double experienceAvg = 0.0;
        double bonusSum = 0.0;
        if (!crew.isEmpty()) {
            int skillSum = 0;
            int experienceSum = 0;
            for (Employee member : crew) {
                skillSum += member.getSkillLevel();
                experienceSum += member.getExperienceYears();
                bonusSum += member.getMissionBonus();
            }
            skillAvg = (double) skillSum / crew.size();
            experienceAvg = (double) experienceSum / crew.size();
        }
        double crewComponent = skillAvg / 10.0 * 0.25;
        double experienceComponent = Math.min(experienceAvg * 0.01, 0.10);
        double difficultyPenalty = difficulty * 0.05;

        double raw = 0.35 + rocketComponent + crewComponent + experienceComponent
                - difficultyPenalty + bonusSum;

        // A rocket that cannot even lift the payload is heavily penalized
        // instead of failing outright here (outright failure is the validator's job).
        if (rocket.getPayloadCapacityKg() < requiredPayloadKg) {
            raw -= 0.30;
        }

        return clamp(raw);
    }

    /** Rolls a 0..1 value against the probability. Tiny helper for readability. */
    public static boolean roll(double probability, double roll) {
        return roll < clamp(probability);
    }

    private static double clamp(double value) {
        return Math.min(MAX_PROBABILITY, Math.max(MIN_PROBABILITY, value));
    }
}
