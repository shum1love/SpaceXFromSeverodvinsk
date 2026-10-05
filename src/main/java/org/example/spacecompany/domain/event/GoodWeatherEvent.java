package org.example.spacecompany.domain.event;

/** Идеальная погода на старте: небольшой бесплатный бонус к шансу. */
public final class GoodWeatherEvent implements GameEvent {

    @Override
    public String getId() {
        return "GOOD_WEATHER";
    }

    @Override
    public String getDescription() {
        return "Идеальная погода на стартовой площадке.";
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public void apply(EventContext context) {
        context.addProbabilityModifier(0.05);
        context.log("СОБЫТИЕ [хорошо]: отличная погода (+5% к шансу).");
    }
}
