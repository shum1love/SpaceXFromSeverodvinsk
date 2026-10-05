package org.example.spacecompany.ui;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Иконка приложения, нарисованная кодом (без файлов-картинок).
 *
 * <p>Ракета рисуется на {@code Canvas} примитивами: скруглённый фон,
 * корпус, нос, иллюминатор, стабилизаторы и пламя. Урок: в JavaFX почти
 * любую простую графику можно создать программно — удобно для иконок,
 * аватарок и учебных экспериментов (поменяй цвета — пересобери — посмотри).
 */
public final class AppIcon {

    private AppIcon() {
    }

    /** Ракета 64×64 на тёмно-синем скруглённом фоне. */
    public static Image create() {
        Canvas canvas = new Canvas(64, 64);
        GraphicsContext g = canvas.getGraphicsContext2D();

        // Фон: скруглённый квадрат.
        g.setFill(Color.web("#1c2733"));
        g.fillRoundRect(0, 0, 64, 64, 16, 16);

        // Пламя.
        g.setFill(Color.web("#f59e0b"));
        g.fillPolygon(new double[]{28, 36, 32}, new double[]{50, 50, 60}, 3);
        g.setFill(Color.web("#ef4444"));
        g.fillPolygon(new double[]{29.5, 34.5, 32}, new double[]{50, 50, 57}, 3);

        // Стабилизаторы.
        g.setFill(Color.web("#ef4444"));
        g.fillPolygon(new double[]{24, 28, 28}, new double[]{42, 42, 52}, 3);
        g.fillPolygon(new double[]{40, 36, 36}, new double[]{42, 42, 52}, 3);

        // Корпус.
        g.setFill(Color.web("#e6edf3"));
        g.fillRoundRect(26, 14, 12, 38, 6, 6);

        // Нос.
        g.setFill(Color.web("#ef4444"));
        g.fillPolygon(new double[]{26, 38, 32}, new double[]{18, 18, 8}, 3);

        // Иллюминатор.
        g.setFill(Color.web("#1d6fb8"));
        g.fillOval(28.5, 26, 7, 7);
        g.setFill(Color.web("#7dd3fc"));
        g.fillOval(30, 27.5, 3, 3);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return canvas.snapshot(params, null);
    }
}
