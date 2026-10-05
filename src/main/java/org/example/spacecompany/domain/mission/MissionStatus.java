package org.example.spacecompany.domain.mission;

/** Состояния жизни миссии. */
public enum MissionStatus {
    /** Создана, но экипаж/ракета ещё не полностью собраны. */
    PLANNED,
    /** Ракета + экипаж назначены и проверены. Можно запускать. */
    READY,
    /** Сейчас летит. */
    IN_PROGRESS,
    /** Завершена успешно. Конечное состояние. */
    SUCCESS,
    /** Завершена провалом. Конечное состояние. */
    FAILED,
    /** Отменена до запуска. Конечное состояние. */
    CANCELLED;

    /** True для SUCCESS, FAILED и CANCELLED. */
    public boolean isTerminal() {
        return this == SUCCESS || this == FAILED || this == CANCELLED;
    }
}
