/**
 * Мозги игры: здесь живут все правила и решения.
 *
 * <p>Кто за что отвечает:
 * <ul>
 *   <li>{@code FinanceService} — все движения денег;</li>
 *   <li>{@code RocketService} — покупка, заправка, ремонт;</li>
 *   <li>{@code EmployeeService} — найм, увольнение, зарплаты;</li>
 *   <li>{@code MissionService} — жизнь миссии + запуски (включая фоновые);</li>
 *   <li>{@code MissionValidator} + {@code ProbabilityCalculator} — чистые функции
 *       проверки и математики (легче всего тестировать);</li>
 *   <li>{@code EventService} — случайности полёта;</li>
 *   <li>{@code StatisticsService} — аналитика (циклы + стримы);</li>
 *   <li>{@code SaveLoadService} — сохранение в файл.</li>
 * </ul>
 *
 * <p>Новичкам: начни с {@code ProbabilityCalculator} (коротко и понятно),
 * потом иди в {@code MissionService} (главный оркестратор).
 */
package org.example.spacecompany.service;
