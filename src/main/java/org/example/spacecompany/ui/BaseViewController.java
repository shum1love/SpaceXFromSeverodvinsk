package org.example.spacecompany.ui;

import javafx.scene.control.TableView;
import org.example.spacecompany.GameSession;
import org.example.spacecompany.exception.SpaceCompanyException;

/**
 * Общий предок экранов: хранит сессию и колбэк «что-то изменилось».
 *
 * <p>Зачем: у всех экранов одинаковые три действия — взять выбранную строку,
 * показать ошибку сервиса, обновиться после изменения. Чтобы не копировать их
 * в 6 контроллеров, они живут здесь. Бизнес-логики тут нет: методы лишь
 * вызывают сервисы (через наследников) и обновляют вид.
 */
public abstract class BaseViewController implements Refreshable {

    protected final GameSession session;
    private final Runnable changed;

    protected BaseViewController(GameSession session, Runnable changed) {
        this.session = session;
        this.changed = changed;
    }

    /**
     * Выбранная строка таблицы или null + окно «сначала выберите».
     * Убирает одинаковые if'ы из всех кнопок.
     */
    protected <T> T requireSelection(TableView<T> table, String what) {
        T selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiUtils.error("Сначала выберите: " + what + ".");
        }
        return selected;
    }

    /** Выполняет действие с сервисом: ловит бизнес-ошибки в окно. */
    protected void attempt(UiAction action, String doneMessage, String title) {
        try {
            action.run();
            afterChange(title, doneMessage);
        } catch (SpaceCompanyException | IllegalArgumentException e) {
            UiUtils.error(e.getMessage());
        }
    }

    /** Обновить свой экран + строку состояния + показать «готово». */
    protected void afterChange(String title, String message) {
        refresh();
        changed.run();
        UiUtils.info(title, message);
    }

    /** Обновить свой экран + строку состояния, без окна. */
    protected void afterChangeSilent() {
        refresh();
        changed.run();
    }

    /** Действие, которое может бросить бизнес-исключение (для attempt). */
    @FunctionalInterface
    protected interface UiAction {
        void run();
    }
}
