package org.example.spacecompany.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import org.example.spacecompany.domain.event.EngineFailureEvent;
import org.example.spacecompany.domain.event.EventContext;
import org.example.spacecompany.domain.event.ExtraRewardEvent;
import org.example.spacecompany.domain.event.FuelLeakEvent;
import org.example.spacecompany.domain.event.GameEvent;
import org.example.spacecompany.domain.event.GoodWeatherEvent;
import org.example.spacecompany.domain.event.InvestorBonusEvent;
import org.example.spacecompany.domain.event.SolarStormEvent;
import org.example.spacecompany.util.GameConfig;

/**
 * Тянет и применяет случайные события полёта.
 *
 * <p>Стартовый набор ({@link EngineFailureEvent}, {@link FuelLeakEvent},
 * {@link GoodWeatherEvent}, {@link InvestorBonusEvent}, {@link SolarStormEvent},
 * {@link ExtraRewardEvent}) — только начало: вызови
 * {@link #registerEvent}, чтобы подключить свой {@link GameEvent}, не трогая
 * ни один другой класс.
 */
public class EventService {

    private final List<GameEvent> events = new ArrayList<>();
    private final Random random;

    /** Creates the service with the default event pool. */
    public EventService() {
        this(new Random());
    }

    /** Creates the service with an explicit generator (tests pass a seeded one). */
    public EventService(Random random) {
        this.random = Objects.requireNonNull(random, "random");
        events.add(new EngineFailureEvent());
        events.add(new FuelLeakEvent());
        events.add(new GoodWeatherEvent());
        events.add(new InvestorBonusEvent());
        events.add(new SolarStormEvent());
        events.add(new ExtraRewardEvent());
    }

    /** Registers an additional event type. */
    public void registerEvent(GameEvent event) {
        events.add(Objects.requireNonNull(event, "event"));
    }

    /**
     * Removes all registered events. Useful for a fully custom event pool
     * (clear, then {@link #registerEvent} your own) and for deterministic tests.
     */
    public void clearEvents() {
        events.clear();
    }

    /** Currently registered events (unmodifiable copy). */
    public List<GameEvent> registeredEvents() {
        return List.copyOf(events);
    }

    /** Draws one random event, or empty when the pool has no events. */
    public Optional<GameEvent> drawRandomEvent() {
        if (events.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(events.get(random.nextInt(events.size())));
    }

    /**
     * Applies up to {@link GameConfig#MAX_EVENTS_PER_FLIGHT} random events to
     * the flight context. Each event fires independently with a 45% chance,
     * so flights usually see 0-2 events.
     *
     * @return the events that fired, in order
     */
    public List<GameEvent> applyRandomEvents(EventContext context) {
        Objects.requireNonNull(context, "context");
        List<GameEvent> fired = new ArrayList<>();
        for (int i = 0; i < GameConfig.MAX_EVENTS_PER_FLIGHT; i++) {
            if (random.nextDouble() < 0.45) {
                drawRandomEvent().ifPresent(event -> {
                    event.apply(context);
                    fired.add(event);
                });
            }
        }
        return fired;
    }
}
