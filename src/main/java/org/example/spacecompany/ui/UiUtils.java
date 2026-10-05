package org.example.spacecompany.ui;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Мелочи для окон: ошибки, информация, вопрос да/нет.
 * Вся бизнес-логика — в сервисах; здесь только показ диалогов.
 */
public final class UiUtils {

    private UiUtils() {
    }

    /** Модальное окно ошибки. Вызывать только из FX-потока. */
    public static void error(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        style(alert);
        alert.showAndWait();
    }

    /** Модальное информационное окно. Вызывать только из FX-потока. */
    public static void info(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        style(alert);
        alert.showAndWait();
    }

    /** Вопрос да/нет. True — «да». Вызывать только из FX-потока. */
    public static boolean confirm(String question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(null);
        alert.setContentText(question);
        style(alert);
        Optional<ButtonType> answer = alert.showAndWait();
        return answer.filter(b -> b == ButtonType.OK).isPresent();
    }

    private static void style(Alert alert) {
        alert.getDialogPane().getStylesheets().add(
                UiUtils.class.getResource("/ui/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog");
    }
}
