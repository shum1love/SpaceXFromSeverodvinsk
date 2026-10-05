package org.example.spacecompany.domain.event;

import java.math.BigDecimal;

/** Солнечная буря выжигает авионику: большой штраф плюс счёт за ремонт. */
public final class SolarStormEvent implements GameEvent {

    @Override
    public String getId() {
        return "SOLAR_STORM";
    }

    @Override
    public String getDescription() {
        return "Солнечная буря: сбои навигации весь полёт.";
    }

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public void apply(EventContext context) {
        context.addProbabilityModifier(-0.10);
        context.addExtraCost(new BigDecimal("600"));
        context.log("СОБЫТИЕ [плохо]: солнечная буря (−10% к шансу, −600 ₽ ремонт авионики).");
    }
}
