package org.example.spacecompany;

import java.math.BigDecimal;
import org.example.spacecompany.TestFixtures.World;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.RocketStatus;
import org.example.spacecompany.exception.InsufficientFundsException;
import org.example.spacecompany.exception.RocketNotReadyException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RocketServiceTest {

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
    void cannotBuyRocketWithoutMoney() {
        World poor = TestFixtures.newWorld(new BigDecimal("100"));
        try {
            assertThrows(InsufficientFundsException.class,
                    () -> poor.rocketService.buyRocket(RocketModel.STARSHIP, "Dream"));
        } finally {
            poor.shutdown();
        }
    }

    @Test
    void balanceDecreasesAfterPurchase() {
        BigDecimal before = world.finance.getBalance();
        Rocket bought = world.rocketService.buyRocket(RocketModel.FALCON_9, "Test-1");

        assertEquals(before.subtract(RocketModel.FALCON_9.getPrice()), world.finance.getBalance());
        assertEquals(1, world.rockets.count());
        assertEquals(RocketStatus.NO_FUEL, bought.getStatus());
    }

    @Test
    void refuelMakesRocketReady() {
        Rocket rocket = world.rocketService.buyRocket(RocketModel.FALCON_1, "Fueled");
        world.rocketService.refuelRocket(rocket.getId());

        assertEquals(rocket.getMaxFuel(), rocket.getFuel());
        assertEquals(RocketStatus.READY, rocket.getStatus());
        assertTrue(rocket.isReadyForLaunch());
    }

    @Test
    void cannotRefuelDestroyedRocket() {
        Rocket rocket = world.rocketService.buyRocket(RocketModel.FALCON_1, "Doomed");
        rocket.markDestroyed();

        assertThrows(RocketNotReadyException.class,
                () -> world.rocketService.refuelRocket(rocket.getId()));
    }

    @Test
    void repairRestoresDamagedRocket() {
        Rocket rocket = world.rocketService.buyRocket(RocketModel.FALCON_1, "Rusty");
        world.rocketService.refuelRocket(rocket.getId());
        rocket.damage(60);
        assertEquals(RocketStatus.DAMAGED, rocket.getStatus());

        world.rocketService.repairRocket(rocket.getId());

        assertEquals(100, rocket.getCondition());
        assertEquals(RocketStatus.READY, rocket.getStatus());
    }

    @Test
    void destroyedRocketCannotBeRepaired() {
        Rocket rocket = world.rocketService.buyRocket(RocketModel.FALCON_1, "Gone");
        rocket.markDestroyed();

        assertThrows(RocketNotReadyException.class,
                () -> world.rocketService.repairRocket(rocket.getId()));
    }

    @Test
    void findReadyRocketsOnlyReturnsFueledHealthyOnes() {
        Rocket ready = world.rocketService.buyRocket(RocketModel.FALCON_1, "Ready");
        world.rocketService.refuelRocket(ready.getId());
        world.rocketService.buyRocket(RocketModel.FALCON_1, "Empty");

        assertEquals(1, world.rocketService.findReadyRockets().size());
        assertEquals(ready.getId(), world.rocketService.findReadyRockets().get(0).getId());
    }
}
