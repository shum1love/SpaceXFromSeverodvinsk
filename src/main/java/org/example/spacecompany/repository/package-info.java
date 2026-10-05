/**
 * Полки для хранения: интерфейсы + реализации в памяти.
 *
 * <p>Учебная суть пакета — связка «интерфейс → реализация → внедрение»:
 * сервисы зависят от {@code RocketRepository} и не знают, что внутри
 * {@code InMemoryRocketRepository} лежит {@code ConcurrentHashMap}.
 * Хранилище можно заменить, не трогая бизнес-логику.
 * Базовый контракт — обобщённый {@code Repository<T, ID>} (пример generics).
 */
package org.example.spacecompany.repository;
