package org.example.spacecompany;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Проверяет общее ядро {@link GameSession}, поверх которого работают
 * И консоль, И JavaFX: сборка, стартовый капитал и стартовый набор.
 * Туулкти JavaFX здесь не нужен — тестируется только ядро без интерфейса.
 */
class GameSessionTest {

    @Test
    void sessionBuildsWithStarterKit() {
        GameSession session = new GameSession("Тест");
        try {
            assertNotNull(session.company());
            assertNotNull(session.finance());
            assertNotNull(session.rockets());
            assertNotNull(session.employees());
            assertNotNull(session.missions());
            assertNotNull(session.statistics());
            assertNotNull(session.saveLoad());

            // Стартовый набор города: 1 ракета + 2 специалиста.
            assertEquals(1, session.company().getRockets().count());
            assertEquals(2, session.company().getEmployees().count());
            assertEquals(0, session.company().getBalance()
                    .compareTo(org.example.spacecompany.util.GameConfig.STARTING_BALANCE));
        } finally {
            session.shutdown();
        }
    }

    @Test
    void blankNameFallsBackToDefault() {
        GameSession session = new GameSession("   ");
        try {
            assertEquals("Северодвинские Звёзды", session.company().getName());
        } finally {
            session.shutdown();
        }
    }
}
