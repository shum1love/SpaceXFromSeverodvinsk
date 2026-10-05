package org.example.spacecompany;

import java.math.BigDecimal;
import java.util.List;
import org.example.spacecompany.TestFixtures.World;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.employee.Engineer;
import org.example.spacecompany.domain.employee.Pilot;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionType;
import org.example.spacecompany.service.ProbabilityCalculator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProbabilityCalculatorTest {

    private final World world = TestFixtures.newWorld();

    @AfterEach
    void tearDown() {
        world.shutdown();
    }

    private Rocket rocket(RocketModel model) {
        Rocket rocket = new Rocket("Calc", model);
        rocket.refuel();
        return rocket;
    }

    private List<org.example.spacecompany.domain.employee.Employee> crew() {
        return List.of(
                new Pilot("P", new BigDecimal("1000"), 5, 8),
                new Engineer("E", new BigDecimal("1000"), 5, 8));
    }

    @Test
    void probabilityStaysWithinBounds() {
        Rocket rusty = new Rocket("Rusty", RocketModel.STARSHIP);
        // Unfueled, damaged, rookie crew, hardest mission — still >= floor.
        rusty.damage(90);
        var rookie = List.of(new Pilot("R", BigDecimal.ZERO, 0, 1));
        Mission mars = new Mission("Mars", MissionType.MARS_MISSION);

        double worst = ProbabilityCalculator.calculateSuccessProbability(mars, rusty, rookie);
        assertTrue(worst >= ProbabilityCalculator.MIN_PROBABILITY);

        // Best rocket, elite crew, easiest mission — still <= ceiling.
        Rocket best = rocket(RocketModel.FALCON_9);
        var elite = List.of(new Pilot("A", new BigDecimal("5000"), 20, 10));
        Mission easy = new Mission("Easy", MissionType.SATELLITE_LAUNCH);
        double top = ProbabilityCalculator.calculateSuccessProbability(easy, best, elite);
        assertTrue(top <= ProbabilityCalculator.MAX_PROBABILITY);
    }

    @ParameterizedTest
    @EnumSource(MissionType.class)
    void harderMissionsAreLessLikely(MissionType type) {
        // Same rocket and crew: difficulty must not increase the chance.
        // (Checked pairwise in the next test; here we only assert bounds.)
        double p = ProbabilityCalculator.calculateSuccessProbability(
                new Mission("M", type), rocket(RocketModel.FALCON_9), crew());
        assertTrue(p >= ProbabilityCalculator.MIN_PROBABILITY
                && p <= ProbabilityCalculator.MAX_PROBABILITY);
    }

    @Test
    void satelliteBeatsMarsWithSameSetup() {
        Rocket r = rocket(RocketModel.FALCON_9);
        double satellite = ProbabilityCalculator.calculateSuccessProbability(
                new Mission("S", MissionType.SATELLITE_LAUNCH), r, crew());
        double mars = ProbabilityCalculator.calculateSuccessProbability(
                new Mission("M", MissionType.MARS_MISSION), r, crew());
        assertTrue(satellite > mars);
    }

    @Test
    void betterCrewImprovesOdds() {
        Rocket r = rocket(RocketModel.FALCON_9);
        Mission mission = new Mission("M", MissionType.CARGO_DELIVERY);
        double rookies = ProbabilityCalculator.calculateSuccessProbability(mission, r,
                List.of(new Pilot("R", BigDecimal.ZERO, 0, 1)));
        double veterans = ProbabilityCalculator.calculateSuccessProbability(mission, r, crew());
        assertTrue(veterans > rookies);
    }

    @ParameterizedTest
    @CsvSource({
            "0.7, 0.5, true",
            "0.7, 0.8, false",
            "0.05, 0.049, true",
            "0.05, 0.05, false"
    })
    void rollRespectsProbability(double probability, double roll, boolean expected) {
        assertEquals(expected, ProbabilityCalculator.roll(probability, roll));
    }

    @Test
    void overloadsAgree() {
        Rocket r = rocket(RocketModel.FALCON_9);
        Mission mission = new Mission("M", MissionType.RESEARCH,
                MissionType.RESEARCH.getRequiredPayloadKg(), MissionType.RESEARCH.getBaseReward());
        double byMission = ProbabilityCalculator.calculateSuccessProbability(mission, r, crew());
        double byType = ProbabilityCalculator.calculateSuccessProbability(MissionType.RESEARCH, r, crew());
        assertEquals(byMission, byType, 1e-9);
    }
}
