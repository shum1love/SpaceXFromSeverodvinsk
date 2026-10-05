package org.example.spacecompany.domain.event;

import java.math.BigDecimal;

/** Неполадки двигателя в полёте: шанс успеха падает, ремонт стоит денег. */
public final class EngineFailureEvent implements GameEvent {

    @Override
    public String getId() {
        return "ENGINE_FAILURE";
    }

    @Override
    public String getDescription() {
        return "Аномалия двигателя: тяга нестабильна.";
    }

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public void apply(EventContext context) {
        context.addProbabilityModifier(-0.12);
        context.addExtraCost(new BigDecimal("700"));
        context.log("СОБЫТИЕ [плохо]: аномалия двигателя (−12% к шансу, −700 ₽ ремонт).");
    }
}
