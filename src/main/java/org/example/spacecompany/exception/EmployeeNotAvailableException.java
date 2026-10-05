package org.example.spacecompany.exception;

/** Бросается, когда сотрудника нельзя назначить (занят, в отпуске, нет). Непроверяемое. */
public class EmployeeNotAvailableException extends SpaceCompanyException {

    public EmployeeNotAvailableException(String message) {
        super(message);
    }
}
