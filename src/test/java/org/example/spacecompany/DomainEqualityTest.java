package org.example.spacecompany;

import java.math.BigDecimal;
import java.util.UUID;
import org.example.spacecompany.TestFixtures.World;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.Engineer;
import org.example.spacecompany.domain.employee.Pilot;
import org.example.spacecompany.domain.finance.Transaction;
import org.example.spacecompany.domain.finance.TransactionType;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DomainEqualityTest {

    private final World world = TestFixtures.newWorld();

    @AfterEach
    void tearDown() {
        world.shutdown();
    }

    @Test
    void rocketsEqualByIdOnly() {
        UUID id = UUID.randomUUID();
        Rocket first = new Rocket(id, "Same", RocketModel.FALCON_9);
        Rocket second = new Rocket(id, "Different name", RocketModel.STARSHIP);
        first.refuel();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        Rocket other = new Rocket("Same", RocketModel.FALCON_9);
        assertNotEquals(first, other);
    }

    @Test
    void employeesEqualByIdOnly() {
        UUID id = UUID.randomUUID();
        Employee first = new Pilot(id, "Alex", new BigDecimal("1000"), 1, 3,
                java.time.LocalDate.now());
        Employee second = new Engineer(id, "Alex", new BigDecimal("9999"), 9, 9,
                java.time.LocalDate.now());

        // Same id → same person, even across subclasses and different fields.
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void missionsEqualByIdOnly() {
        UUID id = UUID.randomUUID();
        Mission first = new Mission(id, "M", MissionType.RESEARCH, 100,
                new BigDecimal("100"), java.time.LocalDateTime.now());
        Mission second = new Mission(id, "Other", MissionType.MARS_MISSION, 5000,
                new BigDecimal("25000"), java.time.LocalDateTime.now());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void transactionsEqualByIdOnly() {
        UUID id = UUID.randomUUID();
        Transaction first = new Transaction(id, TransactionType.INCOME,
                new BigDecimal("10"), "a", java.time.LocalDateTime.now());
        Transaction second = new Transaction(id, TransactionType.EXPENSE,
                new BigDecimal("999"), "b", java.time.LocalDateTime.now());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void companiesEqualByNameCaseInsensitive() {
        assertEquals(world.company, new org.example.spacecompany.domain.Company(
                "test corp", BigDecimal.ZERO, world.rockets, world.employees, world.missions));
    }

    @Test
    void toStringIsInformative() {
        Rocket rocket = new Rocket("Info", RocketModel.FALCON_1);
        assertEquals(true, rocket.toString().contains("Info"));
        Pilot pilot = new Pilot("Info Pilot", new BigDecimal("100"), 1, 1);
        assertEquals(true, pilot.toString().contains("Info Pilot"));
        Mission mission = new Mission("Info Mission", MissionType.RESEARCH);
        assertEquals(true, mission.toString().contains("Info Mission"));
    }
}
