/**
 * Миссии: контракты, которые выполняет компания.
 *
 * <p>{@link org.example.spacecompany.domain.mission.Mission} хранит ссылки
 * (id ракеты + id экипажа), а не сами объекты — так проще сохранять в файл.
 * {@link org.example.spacecompany.domain.mission.MissionResult} — неизменяемый
 * итог полёта (пример {@code record}).
 */
package org.example.spacecompany.domain.mission;
