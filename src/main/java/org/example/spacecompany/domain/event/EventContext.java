package org.example.spacecompany.domain.event;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Изменяемые данные, которые передаются {@link GameEvent} во время полёта.
 *
 * <p>События не трогают компанию и генератор случайностей напрямую.
 * Вместо этого они крутят контекст: понижают/повышают шанс успеха, добавляют
 * бонус, добавляют расходы или дописывают строку в журнал. После всех событий
 * {@code MissionService} применяет накопленное ровно один раз.
 */
public final class EventContext {

    private double probabilityModifier;
    private BigDecimal bonusReward = BigDecimal.ZERO;
    private BigDecimal extraCost = BigDecimal.ZERO;
    private final List<String> logEntries = new ArrayList<>();

    public double getProbabilityModifier() {
        return probabilityModifier;
    }

    public void addProbabilityModifier(double delta) {
        this.probabilityModifier += delta;
    }

    public BigDecimal getBonusReward() {
        return bonusReward;
    }

    public void addBonusReward(BigDecimal amount) {
        if (amount != null) {
            this.bonusReward = this.bonusReward.add(amount);
        }
    }

    public BigDecimal getExtraCost() {
        return extraCost;
    }

    public void addExtraCost(BigDecimal amount) {
        if (amount != null) {
            this.extraCost = this.extraCost.add(amount);
        }
    }

    public void log(String entry) {
        if (entry != null && !entry.isBlank()) {
            logEntries.add(entry);
        }
    }

    public List<String> getLogEntries() {
        return Collections.unmodifiableList(logEntries);
    }
}
