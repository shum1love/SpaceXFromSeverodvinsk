package org.example.spacecompany.domain.event;

import java.math.BigDecimal;

/** Заказчик доплачивает за досрочную доставку. */
public final class ExtraRewardEvent implements GameEvent {

    @Override
    public String getId() {
        return "EXTRA_REWARD";
    }

    @Override
    public String getDescription() {
        return "Заказчик платит бонус за досрочную доставку.";
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public void apply(EventContext context) {
        context.addBonusReward(new BigDecimal("800"));
        context.log("СОБЫТИЕ [хорошо]: бонус за сроки (+800 ₽).");
    }
}
