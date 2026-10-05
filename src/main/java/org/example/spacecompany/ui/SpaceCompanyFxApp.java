package org.example.spacecompany.ui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.spacecompany.GameSession;

/**
 * JavaFX-приложение: окно, сцена, тёмная тема, корневой контроллер.
 *
 * <p>Порядок запуска: создать {@link GameSession} (общее ядро — то же, что у
 * консоли) → загрузить {@code MainView.fxml} → отдать сцену окну.
 * Сессия закрывается при закрытии окна ({@code stop()}).
 */
public class SpaceCompanyFxApp extends Application {

    private GameSession session;

    @Override
    public void start(Stage stage) throws IOException {
        session = new GameSession(null);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/MainView.fxml"));
        loader.setControllerFactory(param -> new MainController(session));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());

        stage.setTitle("Симулятор космической компании — " + session.company().getName());
        stage.getIcons().add(AppIcon.create());
        stage.setMinWidth(960);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        if (session != null) {
            session.shutdown();
        }
    }
}
