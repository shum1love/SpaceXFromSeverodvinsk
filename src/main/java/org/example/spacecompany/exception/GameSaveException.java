package org.example.spacecompany.exception;

import java.io.IOException;

/**
 * Проверяемое исключение для ошибок сохранения/загрузки.
 *
 * <p>Проверяемое — намеренно: ошибки диска, пропавшие файлы и битые данные —
 * это проблемы среды, которые проверками не предотвратить, поэтому компилятор
 * заставляет вызывающего обработать их явно (поймать или объявить).
 * Контраст с непроверяемыми бизнес-исключениями этого пакета.
 */
public class GameSaveException extends Exception {

    public GameSaveException(String message) {
        super(message);
    }

    public GameSaveException(String message, IOException cause) {
        super(message, cause);
    }
}
