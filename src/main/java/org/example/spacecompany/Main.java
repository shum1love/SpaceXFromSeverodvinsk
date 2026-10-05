package org.example.spacecompany;

/**
 * Тонкий запуск: всё делегирует {@link Application}.
 * Бизнес-логики здесь нет — так задумано.
 */
public class Main {

    public static void main(String[] args) {
        new Application().run();
    }
}
