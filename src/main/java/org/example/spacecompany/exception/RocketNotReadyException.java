package org.example.spacecompany.exception;

/** Бросается, когда ракета не в лётном состоянии. Непроверяемое. */
public class RocketNotReadyException extends SpaceCompanyException {

    public RocketNotReadyException(String message) {
        super(message);
    }
}
