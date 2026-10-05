package org.example.spacecompany;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.RocketStatus;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.EmployeeRole;
import org.example.spacecompany.domain.employee.Pilot;
import org.example.spacecompany.domain.employee.Scientist;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionStatus;
import org.example.spacecompany.domain.mission.MissionType;
import org.example.spacecompany.repository.InMemoryEmployeeRepository;
import org.example.spacecompany.repository.InMemoryMissionRepository;
import org.example.spacecompany.repository.InMemoryRocketRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoryTest {

    @Test
    void rocketRepositoryCrud() {
        var repo = new InMemoryRocketRepository();
        Rocket rocket = new Rocket("Repo-1", RocketModel.FALCON_9);

        assertEquals(0, repo.count());
        repo.save(rocket);
        assertEquals(1, repo.count());
        assertTrue(repo.existsById(rocket.getId()));

        Optional<Rocket> found = repo.findById(rocket.getId());
        assertTrue(found.isPresent());
        assertEquals("Repo-1", found.get().getName());

        assertTrue(repo.deleteById(rocket.getId()));
        assertFalse(repo.existsById(rocket.getId()));
        assertTrue(repo.findById(rocket.getId()).isEmpty());
    }

    @Test
    void rocketFinders() {
        var repo = new InMemoryRocketRepository();
        Rocket fueled = new Rocket("Fueled One", RocketModel.FALCON_1);
        fueled.refuel();
        Rocket empty = new Rocket("Empty One", RocketModel.FALCON_1);
        repo.save(fueled);
        repo.save(empty);

        List<Rocket> ready = repo.findByStatus(RocketStatus.READY);
        assertEquals(1, ready.size());
        assertEquals("Fueled One", ready.get(0).getName());

        assertEquals(2, repo.findByNameContaining("one").size());
        assertEquals(1, repo.findByNameContaining("FUELED").size());
        assertEquals(0, repo.findByNameContaining("Starship").size());
    }

    @Test
    void employeeRepositoryFinders() {
        var repo = new InMemoryEmployeeRepository();
        Pilot pilot = new Pilot("Repo Pilot", new BigDecimal("1000"), 1, 3);
        Scientist scientist = new Scientist("Repo Scientist", new BigDecimal("1000"), 1, 3);
        repo.save(pilot);
        repo.save(scientist);

        assertEquals(2, repo.findAvailable().size());
        List<Employee> pilots = repo.findByRole(EmployeeRole.PILOT);
        assertEquals(1, pilots.size());
        assertEquals("Repo Pilot", pilots.get(0).getName());

        pilot.setStatus(org.example.spacecompany.domain.employee.EmployeeStatus.ON_MISSION);
        assertEquals(1, repo.findAvailable().size());
    }

    @Test
    void missionRepositoryFinders() {
        var repo = new InMemoryMissionRepository();
        Mission planned = new Mission("P", MissionType.RESEARCH);
        Mission done = new Mission("D", MissionType.RESEARCH);
        done.setStatus(MissionStatus.SUCCESS);
        repo.save(planned);
        repo.save(done);

        assertEquals(1, repo.findByStatus(MissionStatus.PLANNED).size());
        assertEquals(1, repo.findByStatus(MissionStatus.SUCCESS).size());
        assertEquals(0, repo.findByStatus(MissionStatus.FAILED).size());
    }

    @Test
    void clearRemovesEverything() {
        var rockets = new InMemoryRocketRepository();
        rockets.save(new Rocket("X", RocketModel.FALCON_1));
        rockets.clear();
        assertEquals(0, rockets.count());
    }
}
