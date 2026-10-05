package org.example.spacecompany;

import java.math.BigDecimal;
import java.nio.file.Path;
import org.example.spacecompany.TestFixtures.World;
import org.example.spacecompany.domain.Company;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.employee.Pilot;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionType;
import org.example.spacecompany.exception.GameSaveException;
import org.example.spacecompany.service.SaveLoadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveLoadServiceTest {

    private final World world = TestFixtures.newWorld();
    private final SaveLoadService saveLoad = new SaveLoadService();

    @TempDir
    Path tempDir;

    @AfterEach
    void tearDown() {
        world.shutdown();
    }

    @Test
    void roundTripPreservesEverything() throws Exception {
        Rocket rocket = world.rocketService.buyRocket(RocketModel.FALCON_HEAVY, "Save|Me\nNow");
        world.rocketService.refuelRocket(rocket.getId());
        Pilot pilot = new Pilot("Save Pilot", new BigDecimal("1300"), 3, 6);
        world.employeeService.hire(pilot);
        Mission mission = world.missionService.createMission("Save Mission", MissionType.CARGO_DELIVERY);
        world.missionService.assignRocket(mission.getId(), rocket.getId());

        Path file = tempDir.resolve("game.txt");
        saveLoad.save(world.company, file);
        assertTrue(file.toFile().length() > 0);

        Company loaded = saveLoad.load(file);

        assertEquals(world.company.getName(), loaded.getName());
        assertEquals(0, world.company.getBalance().compareTo(loaded.getBalance()));
        assertEquals(world.company.getFoundedDate(), loaded.getFoundedDate());
        assertEquals(1, loaded.getRockets().count());
        Rocket loadedRocket = loaded.getRockets().findAll().get(0);
        assertEquals(rocket.getId(), loadedRocket.getId());
        assertEquals("Save|Me\nNow", loadedRocket.getName());
        assertEquals(rocket.getFuel(), loadedRocket.getFuel());
        assertEquals(1, loaded.getEmployees().count());
        assertEquals(pilot.getId(), loaded.getEmployees().findAll().get(0).getId());
        assertEquals(1, loaded.getMissions().count());
        Mission loadedMission = loaded.getMissions().findById(mission.getId()).orElseThrow();
        assertEquals(rocket.getId(), loadedMission.getAssignedRocketId());
        assertEquals(world.company.getTransactions().size(), loaded.getTransactions().size());
    }

    @Test
    void restoreFromReplacesLiveCompany() throws Exception {
        world.rocketService.buyRocket(RocketModel.FALCON_1, "Temp");
        Path file = tempDir.resolve("other.txt");
        saveLoad.save(world.company, file);

        World fresh = TestFixtures.newWorld();
        try {
            assertEquals(0, fresh.rockets.count());
            Company loaded = saveLoad.load(file);
            fresh.company.restoreFrom(loaded);

            assertEquals(1, fresh.rockets.count());
            assertEquals(world.company.getName(), fresh.company.getName());
            assertEquals(0, world.company.getBalance().compareTo(fresh.company.getBalance()));
        } finally {
            fresh.shutdown();
        }
    }

    @Test
    void missingFileThrowsCheckedException() {
        Path missing = tempDir.resolve("nope.txt");
        GameSaveException e = assertThrows(GameSaveException.class,
                () -> saveLoad.load(missing));
        assertTrue(e.getMessage().contains("nope.txt"));
    }

    @Test
    void corruptFileThrowsCheckedException() throws Exception {
        Path file = tempDir.resolve("corrupt.txt");
        java.nio.file.Files.writeString(file, "this is not a save file\n[COMPANY]\nname=x\n");

        assertThrows(GameSaveException.class, () -> saveLoad.load(file));
    }
}
