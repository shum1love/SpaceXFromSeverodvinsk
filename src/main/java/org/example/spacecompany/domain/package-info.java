/**
 * Вещи игры: состояние + поведение, без ввода/вывода и сервисов.
 *
 * <p>Что где:
 * <ul>
 *   <li>{@link org.example.spacecompany.domain.Company} — корень: баланс, история, склады;</li>
 *   <li>{@link org.example.spacecompany.domain.Rocket} — ракета и её состояния;</li>
 *   <li>{@code employee} — люди: база + 4 профессии;</li>
 *   <li>{@code mission} — миссии и итог полёта;</li>
 *   <li>{@code finance} — деньги: неизменяемые проводки;</li>
 *   <li>{@code event} — случайные события полёта.</li>
 * </ul>
 *
 * <p>Правило пакета: объект сам охраняет своё состояние (проверки в сеттерах
 * и методах). Новичкам: начни с {@code Rocket} — он самый показательный.
 */
package org.example.spacecompany.domain;
