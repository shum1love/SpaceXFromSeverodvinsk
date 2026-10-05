package org.example.spacecompany.domain.event;

import java.math.BigDecimal;

/** Впечатлённые инвесторы докидывают денег после чистого старта. */
public final class InvestorBonusEvent implements GameEvent {

    @Override
    public String getId() {
        return "INVESTOR_BONUS";
    }

    @Override
    public String getDescription() {
        return "Инвесторам понравилась трансляция старта, добавили бонус.";
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public void apply(EventContext context) {
        context.addBonusReward(new BigDecimal("1200"));
        context.log("СОБЫТИЕ [хорошо]: бонус инвесторов (+1200 ₽).");
    }
}
