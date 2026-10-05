package org.example.spacecompany;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;
import org.example.spacecompany.TestFixtures.World;
import org.example.spacecompany.domain.LaunchRecord;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.RocketStatus;
import org.example.spacecompany.domain.employee.EmployeeStatus;
import org.example.spacecompany.domain.employee.Engineer;
import org.example.spacecompany.domain.employee.Pilot;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionResult;
import org.example.spacecompany.domain.mission.MissionStatus;
import org.example.spacecompany.domain.mission.MissionType;
import org.example.spacecompany.service.ProbabilityCalculator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissionServiceTest {

    private World world;
    private Rocket rocket;
    private Pilot pilot;
    private Engineer engineer;

    @BeforeEach
    void setUp() {
        world = TestFixtures.newWorld();
        rocket = world.rocketService.buyRocket(RocketModel.FALCON_9, "Test Rocket");
        world.rocketService.refuelRocket(rocket.getId());
        pilot = new Pilot("Test Pilot", new BigDecimal("1000"), 5, 8);
        engineer = new Engineer("Test Engineer", new BigDecimal("1000"), 5, 8);
        world.employeeService.hire(pilot);
        world.employeeService.hire(engineer);
    }

    @AfterEach
    void tearDown() {
        world.shutdown();
    }

    /** Creates a READY satellite mission (needs 1 pilot; we assign two for better odds). */
    private Mission readyMission(String name) {
        Mission mission = world.missionService.createMission(name, MissionType.SATELLITE_LAUNCH);
        world.missionService.assignRocket(mission.getId(), rocket.getId());
        world.missionService.assignCrewMember(mission.getId(), pilot.getId());
        world.missionService.assignCrewMember(mission.getId(), engineer.getId());
        // SATELLITE_LAUNCH needs a PILOT; engineer is extra muscle.
        assertEquals(MissionStatus.READY, mission.getStatus());
        return mission;
    }

    private double probabilityOf(Mission mission) {
        return ProbabilityCalculator.calculateSuccessProbability(mission, rocket,
                List.of(pilot, engineer));
    }

    @Test
    void rewardIsPaidAfterSuccessfulMission() {
        Mission mission = readyMission("Win-1");
        double probability = probabilityOf(mission);
        long seed = TestFixtures.seedFor(true, probability);
        BigDecimal before = world.finance.getBalance();

        MissionResult result = world.missionService.launch(mission.getId(), new Random(seed));

        assertTrue(result.success());
        assertEquals(MissionStatus.SUCCESS, mission.getStatus());
        // Balance: launch cost paid, full reward received.
        BigDecimal expected = before
                .subtract(RocketModel.FALCON_9.getLaunchCost())
                .add(mission.getReward());
        assertEquals(0, expected.compareTo(world.finance.getBalance()));
        assertEquals(0, mission.getReward().compareTo(result.rewardPaid()));
    }

    @Test
    void failureIsHandledCorrectly() {
        Mission mission = readyMission("Lose-1");
        double probability = probabilityOf(mission);
        long seed = TestFixtures.seedFor(false, probability);
        BigDecimal before = world.finance.getBalance();

        MissionResult result = world.missionService.launch(mission.getId(), new Random(seed));

        assertFalse(result.success());
        assertEquals(MissionStatus.FAILED, mission.getStatus());
        // No reward, only the launch cost was spent (plus possible incident costs).
        assertEquals(BigDecimal.ZERO, result.rewardPaid());
        assertTrue(world.finance.getBalance().compareTo(
                before.subtract(RocketModel.FALCON_9.getLaunchCost())) <= 0);
        // Rocket survived or was destroyed — but it must not be stuck ON_MISSION.
        assertTrue(rocket.getStatus() == RocketStatus.NO_FUEL
                || rocket.getStatus() == RocketStatus.DAMAGED
                || rocket.getStatus() == RocketStatus.DESTROYED);
        assertNotNull(mission.getResultLog());
    }

    @Test
    void crewIsReleasedAndGainsExperience() {
        Mission mission = readyMission("Crew-1");
        double probability = probabilityOf(mission);
        int expBefore = pilot.getExperienceYears();

        world.missionService.launch(mission.getId(), new Random(TestFixtures.seedFor(true, probability)));

        assertEquals(EmployeeStatus.AVAILABLE, pilot.getStatus());
        assertEquals(EmployeeStatus.AVAILABLE, engineer.getStatus());
        assertEquals(expBefore + 1, pilot.getExperienceYears());
    }

    @Test
    void fuelIsBurnedAndCountersGrow() {
        Mission mission = readyMission("Fuel-1");
        double probability = probabilityOf(mission);
        int launchesBefore = rocket.getLaunchCount();

        world.missionService.launch(mission.getId(), new Random(TestFixtures.seedFor(true, probability)));

        assertEquals(0, rocket.getFuel());
        assertEquals(launchesBefore + 1, rocket.getLaunchCount());
        assertEquals(launchesBefore + 1, rocket.getSuccessCount());
    }

    @Test
    void launchHistoryRecorded() {
        Mission mission = readyMission("Hist-1");
        double probability = probabilityOf(mission);

        world.missionService.launch(mission.getId(), new Random(TestFixtures.seedFor(true, probability)));

        List<LaunchRecord> history = world.company.getLaunchHistory();
        assertEquals(1, history.size());
        assertEquals("Hist-1", history.get(0).getMissionName());
        assertTrue(history.get(0).isSuccess());
    }

    @Test
    void asyncLaunchCompletes() throws Exception {
        Mission mission = readyMission("Async-1");
        double probability = probabilityOf(mission);

        // Seeded roll is not injectable into launchAsync(); instead we only
        // assert that the background flight finishes and records an outcome.
        Future<MissionResult> future = world.missionService.launchAsync(mission.getId());
        MissionResult result = future.get();

        assertNotNull(result);
        assertTrue(mission.getStatus().isTerminal());
        assertEquals(1, world.missionService.getCompletedFlightCount());
    }

    @Test
    void concurrentLaunchOfAllReady() throws Exception {
        Mission first = readyMission("Race-1");
        // Second mission needs its own rocket + crew.
        Rocket rocket2 = world.rocketService.buyRocket(RocketModel.FALCON_1, "Second");
        world.rocketService.refuelRocket(rocket2.getId());
        Pilot pilot2 = new Pilot("Second Pilot", new BigDecimal("1000"), 2, 4);
        world.employeeService.hire(pilot2);
        Mission second = world.missionService.createMission("Race-2", MissionType.SATELLITE_LAUNCH);
        world.missionService.assignRocket(second.getId(), rocket2.getId());
        world.missionService.assignCrewMember(second.getId(), pilot2.getId());

        var futures = world.missionService.launchAllReady();
        assertEquals(2, futures.size());
        for (Future<MissionResult> future : futures) {
            assertNotNull(future.get());
        }
        assertTrue(first.getStatus().isTerminal());
        assertTrue(second.getStatus().isTerminal());
        assertEquals(2, world.missionService.getCompletedFlightCount());
    }

    @Test
    void doubleLaunchIsRejected() {
        Mission mission = readyMission("Once-1");
        double probability = probabilityOf(mission);
        world.missionService.launch(mission.getId(), new Random(TestFixtures.seedFor(true, probability)));

        org.junit.jupiter.api.Assertions.assertThrows(
                org.example.spacecompany.exception.MissionValidationException.class,
                () -> world.missionService.launch(mission.getId(), new Random(1)));
    }
}
