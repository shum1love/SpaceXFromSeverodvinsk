package org.example.spacecompany.ui;

import javafx.application.Application;

/**
 * Точка входа графической версии.
 *
 * <p>Отдельный класс (не наследник {@code Application}) — стандартный приём:
 * так запуск работает и в classpath-режиме без модулей, и из собранного
 * jpackage-пакета. Метод один: передать управление JavaFX.
 *
 * <p>Запуск для разработки: {@code ./u} (или {@code mvn javafx:run}).
 */
public class FxLauncher {

    public static void main(String[] args) {
        Application.launch(SpaceCompanyFxApp.class, args);
    }
}
