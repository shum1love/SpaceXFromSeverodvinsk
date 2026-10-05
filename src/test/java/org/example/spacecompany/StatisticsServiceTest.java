package org.example.spacecompany;

import java.math.BigDecimal;
import org.example.spacecompany.TestFixtures.World;
import org.example.spacecompany.domain.LaunchRecord;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.Engineer;
import org.example.spacecompany.domain.employee.Pilot;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionStatus;
import org.example.spacecompany.domain.mission.MissionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatisticsServiceTest {

    private World world;

    @BeforeEach
    void setUp() {
        world = TestFixtures.newWorld();
    }

    @AfterEach
    void tearDown() {
        world.shutdown();
    }

    @Test
    void successRateFromHistory() {
        assertEquals(0.0, world.statistics.successRate());

        world.company.addLaunchRecord(new LaunchRecord("A", "R1", true, new BigDecimal("100")));
        world.company.addLaunchRecord(new LaunchRecord("B", "R1", false, new BigDecimal("-50")));
        world.company.addLaunchRecord(new LaunchRecord("C", "R2", true, new BigDecimal("200")));

        assertEquals(2.0 / 3.0, world.statistics.successRate(), 1e-9);
        assertEquals(3, world.statistics.totalFlights());
        assertEquals(2, world.statistics.successfulFlights());
    }

    @Test
    void missionsGroupedByStatus() {
        Mission planned = world.missionService.createMission("S1", MissionType.RESEARCH);
        Mission done = world.missionService.createMission("S2", MissionType.RESEARCH);
        done.setStatus(MissionStatus.SUCCESS);

        var grouped = world.statistics.missionsByStatus();
        assertEquals(1L, grouped.get(MissionStatus.PLANNED));
        assertEquals(1L, grouped.get(MissionStatus.SUCCESS));
        assertEquals(2, grouped.size());
    }

    @Test
    void mostReliableAndBestLookups() {
        Rocket rusty = world.rocketService.buyRocket(RocketModel.STARSHIP, "Rusty");
        rusty.damage(40);
        world.rocketService.buyRocket(RocketModel.FALCON_9, "Shiny");

        assertTrue(world.statistics.mostReliableRocket().isPresent());
        // Falcon 9 at 100% beats a damaged Starship despite lower base reliability.
        assertEquals("Shiny", world.statistics.mostReliableRocket().get().getName());

        Pilot rookie = new Pilot("Rookie", new BigDecimal("800"), 0, 2);
        Engineer veteran = new Engineer("Veteran", new BigDecimal("2000"), 10, 9);
        world.employeeService.hire(rookie);
        world.employeeService.hire(veteran);

        Employee best = world.statistics.bestEmployee().orElseThrow();
        assertEquals("Veteran", best.getName());
        assertEquals(2, world.statistics.topEarners(5).size());
        assertEquals("Veteran", world.statistics.topEarners(1).get(0).getName());
    }

    @Test
    void reportContainsKeyFigures() {
        world.missionService.createMission("Rep", MissionType.SATELLITE_LAUNCH);
        world.company.addLaunchRecord(new LaunchRecord("Rep", "R", true, new BigDecimal("3000")));

        String report = world.statistics.buildReport();
        assertTrue(report.contains("Полётов: 1"));
        assertTrue(report.contains("успешных"));
    }
}
