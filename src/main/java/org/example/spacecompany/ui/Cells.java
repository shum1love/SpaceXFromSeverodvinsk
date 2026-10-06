package org.example.spacecompany.ui;

import java.util.function.Function;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Фабрики ячеек таблиц: текст и цветные «пилюли» статусов.
 *
 * <p>Зачем: иначе в каждом контроллере копируется один и тот же
 * {@code setCellValueFactory(... new SimpleStringProperty ...)}.
 * Здесь — два generic-метода на все таблицы. Пример учебных generics
 * и функциональных интерфейсов прямо в UI-коде.
 */
public final class Cells {

    private Cells() {
    }

    /** Простая текстовая колонка: значение берёт функция от строки. */
    public static <T> void text(TableColumn<T, String> column, Function<T, String> textOf) {
        column.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(textOf.apply(cell.getValue())));
    }

    /**
     * Текстовая колонка с цветом по строке: деньги (доход/расход),
     * топливо (полный бак/пустой) и т.п. Цвет задаёт функция от строки.
     */
    public static <T> void coloredText(TableColumn<T, String> column,
                                       Function<T, String> textOf,
                                       Function<T, javafx.scene.paint.Paint> colorOf) {
        column.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(textOf.apply(cell.getValue())));
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String text, boolean empty) {
                super.updateItem(text, empty);
                if (empty) {
                    setText(null);
                } else {
                    @SuppressWarnings("unchecked")
                    T row = (T) getTableRow().getItem();
                    setText(text);
                    if (row != null) {
                        setTextFill(colorOf.apply(row));
                    }
                }
            }
        });
    }

    /**
     * Колонка-«пилюля»: цветной ярлык (статус ракеты, миссии, сотрудника).
     *
     * @param textOf  текст ярлыка
     * @param styleOf CSS-класс ярлыка (например, "pill-ready")
     */
    public static <T> void pill(TableColumn<T, String> column,
                                Function<T, String> textOf,
                                Function<T, String> styleOf) {
        Callback<TableColumn<T, String>, TableCell<T, String>> factory = col -> new TableCell<>() {
            private final Label pill = new Label();

            @Override
            protected void updateItem(String text, boolean empty) {
                super.updateItem(text, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    @SuppressWarnings("unchecked")
                    T row = (T) getTableRow().getItem();
                    if (row == null) {
                        setGraphic(null);
                    } else {
                        pill.setText(textOf.apply(row));
                        pill.getStyleClass().setAll("pill", styleOf.apply(row));
                        setGraphic(pill);
                    }
                }
            }
        };
        column.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(textOf.apply(cell.getValue())));
        column.setCellFactory(factory);
    }

    /** CSS-класс пилюли по статусу ракеты. */
    public static String rocketPill(org.example.spacecompany.domain.RocketStatus status) {
        return switch (status) {
            case READY -> "pill-ready";
            case NO_FUEL -> "pill-wait";
            case IN_MAINTENANCE -> "pill-info";
            case DAMAGED -> "pill-warn";
            case DESTROYED -> "pill-bad";
            case ON_MISSION -> "pill-fly";
        };
    }

    /** CSS-класс пилюли по статусу миссии. */
    public static String missionPill(org.example.spacecompany.domain.mission.MissionStatus status) {
        return switch (status) {
            case PLANNED -> "pill-wait";
            case READY -> "pill-ready";
            case IN_PROGRESS -> "pill-fly";
            case SUCCESS -> "pill-good";
            case FAILED -> "pill-bad";
            case CANCELLED -> "pill-mute";
        };
    }

    /** CSS-класс пилюли по статусу сотрудника. */
    public static String employeePill(org.example.spacecompany.domain.employee.EmployeeStatus status) {
        return switch (status) {
            case AVAILABLE -> "pill-ready";
            case ON_MISSION -> "pill-fly";
            case ON_LEAVE -> "pill-wait";
        };
    }

    /** Человеческое имя статуса ракеты (вместо READY). */
    public static String rocketName(org.example.spacecompany.domain.RocketStatus status) {
        return switch (status) {
            case READY -> "Готова";
            case NO_FUEL -> "Нет топлива";
            case IN_MAINTENANCE -> "Обслуживание";
            case DAMAGED -> "Повреждена";
            case DESTROYED -> "Уничтожена";
            case ON_MISSION -> "На миссии";
        };
    }

    /** Человеческое имя статуса миссии. */
    public static String missionName(org.example.spacecompany.domain.mission.MissionStatus status) {
        return switch (status) {
            case PLANNED -> "Запланирована";
            case READY -> "Готова";
            case IN_PROGRESS -> "Летит";
            case SUCCESS -> "Успех";
            case FAILED -> "Провал";
            case CANCELLED -> "Отменена";
        };
    }

    /** Человеческое имя статуса сотрудника. */
    public static String employeeName(org.example.spacecompany.domain.employee.EmployeeStatus status) {
        return switch (status) {
            case AVAILABLE -> "Доступен";
            case ON_MISSION -> "На миссии";
            case ON_LEAVE -> "В отпуске";
        };
    }

    /** Человеческое имя роли сотрудника. */
    public static String roleName(org.example.spacecompany.domain.employee.EmployeeRole role) {
        return switch (role) {
            case ENGINEER -> "Инженер";
            case PILOT -> "Пилот";
            case SCIENTIST -> "Учёный";
            case MISSION_SPECIALIST -> "Специалист";
        };
    }
}
