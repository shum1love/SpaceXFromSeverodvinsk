package org.example.spacecompany.console;

import org.example.spacecompany.service.StatisticsService;
import org.example.spacecompany.util.InputReader;

/** Подменю «Статистика»: печатает аналитический отчёт. */
public class StatisticsMenu {

    private final StatisticsService statistics;
    private final InputReader input;

    public StatisticsMenu(StatisticsService statistics, InputReader input) {
        this.statistics = statistics;
        this.input = input;
    }

    public void show() {
        MenuIO.printHeader("Статистика");
        MenuIO.info(statistics.buildReport());
        MenuIO.pause(input);
    }
}
