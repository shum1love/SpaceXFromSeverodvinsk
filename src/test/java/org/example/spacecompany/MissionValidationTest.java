package org.example.spacecompany;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.example.spacecompany.TestFixtures.World;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.employee.EmployeeStatus;
import org.example.spacecompany.domain.employee.Engineer;
import org.example.spacecompany.domain.employee.Pilot;
import org.example.spacecompany.domain.employee.Scientist;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionType;
import org.example.spacecompany.exception.EmployeeNotAvailableException;
import org.example.spacecompany.exception.MissionValidationException;
import org.example.spacecompany.service.MissionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissionValidationTest {

    private World world;
    private Rocket rocket;
    private Pilot pilot;
    private Engineer engineer;
    private Scientist scientist;

    @BeforeEach
    void setUp() {
        world = TestFixtures.newWorld();
        rocket = world.rocketService.buyRocket(RocketModel.FALCON_HEAVY, "Validator");
        world.rocketService.refuelRocket(rocket.getId());
        pilot = new Pilot("Test Pilot", new BigDecimal("1000"), 3, 5);
        engineer = new Engineer("Test Engineer", new BigDecimal("1000"), 3, 5);
        scientist = new Scientist("Test Scientist", new BigDecimal("1000"), 3, 5);
        world.employeeService.hire(pilot);
        world.employeeService.hire(engineer);
        world.employeeService.hire(scientist);
    }

    @AfterEach
    void tearDown() {
        world.shutdown();
    }

    private Mission cargoMission() {
        return new Mission("Cargo-1", MissionType.CARGO_DELIVERY);
    }

    @Test
    void validSetupPasses() {
        assertDoesNotThrow(() -> MissionValidator.validateForLaunch(
                cargoMission(), rocket, List.of(pilot, engineer)));
    }

    @Test
    void cannotLaunchWithoutFuel() {
        rocket.consumeFuel(rocket.getFuel());
        assertTrue(rocket.getStatus() != org.example.spacecompany.domain.RocketStatus.READY);

        assertThrows(MissionValidationException.class, () -> MissionValidator.validateForLaunch(
                cargoMission(), rocket, List.of(pilot, engineer)));
    }

    @Test
    void cannotLaunchDestroyedRocket() {
        rocket.markDestroyed();

        assertThrows(MissionValidationException.class, () -> MissionValidator.validateForLaunch(
                cargoMission(), rocket, List.of(pilot, engineer)));
    }

    @Test
    void cannotLaunchDamagedRocket() {
        rocket.damage(80);

        assertThrows(MissionValidationException.class, () -> MissionValidator.validateForLaunch(
                cargoMission(), rocket, List.of(pilot, engineer)));
    }

    @Test
    void payloadMustNotExceedCapacity() {
        Mission heavy = new Mission("Heavy", MissionType.CARGO_DELIVERY, 999_999, new BigDecimal("100"));

        assertThrows(MissionValidationException.class, () ->
                MissionValidator.validateForLaunch(heavy, rocket, List.of(pilot, engineer)));
    }

    @Test
    void missingRoleFailsValidation() {
        // CARGO_DELIVERY needs PILOT + ENGINEER; two pilots are not enough.
        Pilot second = new Pilot("Second Pilot", new BigDecimal("1000"), 1, 3);
        world.employeeService.hire(second);

        assertThrows(MissionValidationException.class, () -> MissionValidator.validateForLaunch(
                cargoMission(), rocket, List.of(pilot, second)));
    }

    @Test
    void specialistCoversMissingRole() {
        var specialist = new org.example.spacecompany.domain.employee.MissionSpecialist(
                "Flex", new BigDecimal("1000"), 2, 5);
        world.employeeService.hire(specialist);

        assertDoesNotThrow(() -> MissionValidator.validateForLaunch(
                cargoMission(), rocket, List.of(pilot, specialist)));
    }

    @Test
    void busyCrewMemberFailsValidation() {
        pilot.setStatus(EmployeeStatus.ON_MISSION);

        assertThrows(MissionValidationException.class, () -> MissionValidator.validateForLaunch(
                cargoMission(), rocket, List.of(pilot, engineer)));
    }

    @Test
    void tooSmallCrewFailsValidation() {
        assertThrows(MissionValidationException.class, () ->
                MissionValidator.validateForLaunch(cargoMission(), rocket, List.of(pilot)));
    }

    @Test
    void cannotAssignBusyEmployeeThroughService() {
        Mission mission = world.missionService.createMission("Busy-1", MissionType.SATELLITE_LAUNCH);
        pilot.setStatus(EmployeeStatus.ON_MISSION);

        assertThrows(MissionValidationException.class,
                () -> world.missionService.assignCrewMember(mission.getId(), pilot.getId()));
    }

    @Test
    void cannotFireEmployeeOnMission() {
        pilot.setStatus(EmployeeStatus.ON_MISSION);

        assertThrows(EmployeeNotAvailableException.class, () -> world.employeeService.fire(pilot.getId()));
    }

    @ParameterizedTest
    @EnumSource(value = MissionType.class, names = {"MOON_MISSION", "MARS_MISSION"})
    void hardMissionsNeedThreeRoles(MissionType type) {
        Mission mission = new Mission("Hard", type);
        // Pilot + engineer only: scientist missing.
        assertThrows(MissionValidationException.class, () ->
                MissionValidator.validateForLaunch(mission, rocket, List.of(pilot, engineer)));
        // Full crew (4 people, all roles covered) passes.
        var specialist = new org.example.spacecompany.domain.employee.MissionSpecialist(
                "Flex", new BigDecimal("1000"), 2, 5);
        world.employeeService.hire(specialist);
        List<org.example.spacecompany.domain.employee.Employee> fullCrew =
                List.of(pilot, engineer, scientist, specialist);
        if (rocket.getPayloadCapacityKg() >= mission.getRequiredPayloadKg()) {
            assertDoesNotThrow(() -> MissionValidator.validateForLaunch(mission, rocket, fullCrew));
        }
    }

    @Test
    void unknownCrewIdRejected() {
        Mission mission = world.missionService.createMission("Ghost", MissionType.SATELLITE_LAUNCH);
        world.missionService.assignRocket(mission.getId(), rocket.getId());
        mission.assignCrewMember(UUID.randomUUID());

        assertThrows(MissionValidationException.class,
                () -> world.missionService.launch(mission.getId(), new java.util.Random(7)));
    }
}
