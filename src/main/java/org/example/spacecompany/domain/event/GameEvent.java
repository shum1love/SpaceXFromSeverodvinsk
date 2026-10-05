package org.example.spacecompany.domain.event;

/**
 * Случайное происшествие, которое может случиться в полёте.
 *
 * <p>Чтобы добавить новое событие: реализуй этот интерфейс и зарегистрируй
 * экземпляр в {@link org.example.spacecompany.service.EventService#registerEvent(GameEvent)}.
 * Больше ничего менять не надо (принцип открытости/закрытости без магии фреймворков:
 * просто интерфейс плюс список).
 */
public interface GameEvent {

    /** Short machine-friendly id, e.g. "ENGINE_FAILURE". */
    String getId();

    /** Human-readable one-line description shown in the flight log. */
    String getDescription();

    /** True for good news (bonus, tailwind), false for bad news. */
    boolean isPositive();

    /** Mutates the flight {@code context} (probability, money, log). */
    void apply(EventContext context);
}
