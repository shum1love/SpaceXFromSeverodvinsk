package org.example.spacecompany.domain.employee;

/** Доступность сотрудника. */
public enum EmployeeStatus {
    /** Можно назначать на миссию. */
    AVAILABLE,
    /** Сейчас летит. Назначить на другую миссию нельзя. */
    ON_MISSION,
    /** В отпуске. До возвращения назначать нельзя. */
    ON_LEAVE
}
