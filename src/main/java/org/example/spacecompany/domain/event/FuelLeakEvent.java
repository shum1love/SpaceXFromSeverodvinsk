package org.example.spacecompany.domain.event;

import java.math.BigDecimal;

/** Утечка топлива: часть награды уйдёт на аварийные запасы. */
public final class FuelLeakEvent implements GameEvent {

    @Override
    public String getId() {
        return "FUEL_LEAK";
    }

    @Override
    public String getDescription() {
        return "Небольшая утечка топлива в магистралях.";
    }

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public void apply(EventContext context) {
        context.addProbabilityModifier(-0.07);
        context.addExtraCost(new BigDecimal("400"));
        context.log("СОБЫТИЕ [плохо]: утечка топлива (−7% к шансу, −400 ₽ на резервы).");
    }
}
