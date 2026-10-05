package org.example.spacecompany.exception;

/** Бросается, когда миссия не прошла предстартовую проверку. Непроверяемое. */
public class MissionValidationException extends SpaceCompanyException {

    public MissionValidationException(String message) {
        super(message);
    }
}
