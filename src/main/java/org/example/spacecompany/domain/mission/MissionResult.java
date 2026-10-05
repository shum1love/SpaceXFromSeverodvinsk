package org.example.spacecompany.domain.mission;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Неизменяемый итог одного выполненного полёта.
 *
 * @param missionId   какая миссия летала
 * @param success     успех или провал
 * @param message     краткий итог человеческим языком
 * @param rewardPaid  деньги, начисленные компании (при провале — ноль)
 * @param extraCost   непредвиденные расходы (ремонт, штрафы)
 * @param eventLog    события, случившиеся в полёте
 * @param completedAt когда полёт завершился
 * @param duration    сколько длился полёт (игровое время)
 */
public record MissionResult(
        UUID missionId,
        boolean success,
        String message,
        BigDecimal rewardPaid,
        BigDecimal extraCost,
        List<String> eventLog,
        LocalDateTime completedAt,
        Duration duration
) {
    public MissionResult {
        Objects.requireNonNull(missionId, "missionId");
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(rewardPaid, "rewardPaid");
        Objects.requireNonNull(extraCost, "extraCost");
        Objects.requireNonNull(completedAt, "completedAt");
        Objects.requireNonNull(duration, "duration");
        eventLog = eventLog == null ? List.of() : Collections.unmodifiableList(eventLog);
    }

    /** Net effect on the balance: reward minus extra costs. */
    public BigDecimal netProfit() {
        return rewardPaid.subtract(extraCost);
    }

    @Override
    public String toString() {
        return "Итог полёта{миссия=%s, успех=%s, награда=%s, доп. расходы=%s, длительность=%s, сообщение='%s'}"
                .formatted(missionId.toString().substring(0, 8), success,
                        rewardPaid, extraCost, duration, message);
    }
}
