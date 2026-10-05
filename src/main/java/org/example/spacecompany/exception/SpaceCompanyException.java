package org.example.spacecompany.exception;

/**
 * База всех нарушений бизнес-правил симулятора.
 *
 * <p>Непроверяемые — намеренно: такие исключения означают, что операция
 * невозможна в текущем состоянии (нет денег, ракета не готова, экипаж занят).
 * Их предотвращают проверками или ловят на границе интерфейса, а не тащат
 * через все сигнатуры. Единственное проверяемое исключение проекта —
 * {@link GameSaveException}: genuinely непредсказуемые сбои ввода/вывода.
 */
public class SpaceCompanyException extends RuntimeException {

    public SpaceCompanyException(String message) {
        super(message);
    }

    public SpaceCompanyException(String message, Throwable cause) {
        super(message, cause);
    }
}
