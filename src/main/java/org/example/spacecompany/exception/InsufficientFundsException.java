package org.example.spacecompany.exception;

/** Бросается, когда на балансе не хватает на платёж. Непроверяемое. */
public class InsufficientFundsException extends SpaceCompanyException {

    public InsufficientFundsException(String message) {
        super(message);
    }
}
