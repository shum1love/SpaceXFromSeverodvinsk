package org.example.spacecompany;

import java.math.BigDecimal;
import org.example.spacecompany.TestFixtures.World;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.EmployeeRole;
import org.example.spacecompany.domain.employee.Engineer;
import org.example.spacecompany.domain.employee.Pilot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeServiceTest {

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
    void hireAndFire() {
        Pilot pilot = new Pilot("Hire Me", new BigDecimal("900"), 1, 4);
        world.employeeService.hire(pilot);

        assertEquals(1, world.employees.count());
        assertTrue(world.employeeService.fire(pilot.getId()));
        assertEquals(0, world.employees.count());
        // Firing again removes nothing.
        assertFalse(world.employees.deleteById(pilot.getId()));
    }

    @Test
    void paySalariesDeductsTotal() {
        world.employeeService.hire(new Pilot("P1", new BigDecimal("1000"), 1, 3));
        world.employeeService.hire(new Engineer("E1", new BigDecimal("1500"), 2, 4));
        BigDecimal before = world.finance.getBalance();

        BigDecimal paid = world.employeeService.payMonthlySalaries();

        assertEquals(0, new BigDecimal("2500").compareTo(paid));
        assertEquals(0, before.subtract(new BigDecimal("2500")).compareTo(world.finance.getBalance()));
    }

    @Test
    void trainingCapsAtMaxSkill() {
        Pilot pilot = new Pilot("Trainee", new BigDecimal("500"), 0, 9);
        world.employeeService.hire(pilot);

        world.employeeService.train(pilot.getId());
        assertEquals(10, pilot.getSkillLevel());
        // Training beyond 10 stays at 10, no exception.
        world.employeeService.train(pilot.getId());
        assertEquals(10, pilot.getSkillLevel());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10})
    void skillLevelsAccepted(int skill) {
        Pilot pilot = new Pilot("S" + skill, new BigDecimal("500"), 0, skill);
        world.employeeService.hire(pilot);
        assertEquals(skill, pilot.getSkillLevel());
    }

    @Test
    void findAvailablePilotsSortedBySkill() {
        Pilot weak = new Pilot("Weak", new BigDecimal("500"), 0, 2);
        Pilot strong = new Pilot("Strong", new BigDecimal("900"), 3, 8);
        world.employeeService.hire(weak);
        world.employeeService.hire(strong);

        var pilots = world.employeeService.findAvailablePilots();
        assertEquals(2, pilots.size());
        assertEquals("Strong", pilots.get(0).getName());
    }

    @Test
    void polymorphismWorkAndBonus() {
        Employee pilot = new Pilot("Poly", new BigDecimal("500"), 1, 5);
        Employee engineer = new Engineer("Morph", new BigDecimal("500"), 1, 5);

        assertEquals(EmployeeRole.PILOT, pilot.getRole());
        assertEquals(EmployeeRole.ENGINEER, engineer.getRole());
        // Один и тот же объявленный тип, разное поведение: переопределение в действии.
        assertTrue(pilot.work().contains("Пилот"));
        assertTrue(engineer.work().contains("Инженер"));
        assertTrue(pilot.getMissionBonus() > 0 && engineer.getMissionBonus() > 0);
    }
}
