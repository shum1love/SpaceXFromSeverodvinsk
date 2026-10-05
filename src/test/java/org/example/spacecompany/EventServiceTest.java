package org.example.spacecompany;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.example.spacecompany.TestFixtures.World;
import org.example.spacecompany.domain.event.EventContext;
import org.example.spacecompany.domain.event.GameEvent;
import org.example.spacecompany.service.EventService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventServiceTest {

    private final World world = TestFixtures.newWorld(new BigDecimal("100000"), false);

    @AfterEach
    void tearDown() {
        world.shutdown();
    }

    @Test
    void defaultPoolIsNotEmpty() {
        assertTrue(!world.events.registeredEvents().isEmpty());
    }

    @Test
    void drawReturnsPresentWhenPoolHasEvents() {
        Optional<GameEvent> drawn = world.events.drawRandomEvent();
        assertTrue(drawn.isPresent());
    }

    @Test
    void customEventCanBeRegistered() {
        EventService events = new EventService(new Random(1));
        events.clearEvents();
        assertTrue(events.drawRandomEvent().isEmpty());

        events.registerEvent(new GameEvent() {
            @Override
            public String getId() {
                return "TEST_BONUS";
            }

            @Override
            public String getDescription() {
                return "Test bonus.";
            }

            @Override
            public boolean isPositive() {
                return true;
            }

            @Override
            public void apply(EventContext context) {
                context.addProbabilityModifier(0.10);
                context.addBonusReward(new BigDecimal("100"));
                context.log("test event fired");
            }
        });

        Optional<GameEvent> drawn = events.drawRandomEvent();
        assertTrue(drawn.isPresent());
        assertEquals("TEST_BONUS", drawn.get().getId());

        EventContext context = new EventContext();
        drawn.get().apply(context);
        assertEquals(0.10, context.getProbabilityModifier(), 1e-9);
        assertEquals(0, new BigDecimal("100").compareTo(context.getBonusReward()));
        assertEquals(1, context.getLogEntries().size());
    }

    @Test
    void randomFlightEventsStayBounded() {
        // With the default pool and a fixed seed the outcome is deterministic.
        EventService events = new EventService(new Random(7));
        EventContext context = new EventContext();
        List<GameEvent> fired = events.applyRandomEvents(context);

        assertTrue(fired.size() <= org.example.spacecompany.util.GameConfig.MAX_EVENTS_PER_FLIGHT);
        assertEquals(fired.size(), context.getLogEntries().size());
    }
}
