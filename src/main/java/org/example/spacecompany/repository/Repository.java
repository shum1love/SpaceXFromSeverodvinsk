package org.example.spacecompany.repository;

import java.util.List;
import java.util.Optional;

/**
 * Минимальная обобщённая абстракция над хранилищем сущностей.
 *
 * <p>Пример generics с двумя параметрами: {@code T} — тип сущности,
 * {@code ID} — тип идентификатора. Сервисы зависят от этого интерфейса,
 * а не от реализации в памяти, поэтому хранилище можно заменить,
 * не трогая бизнес-логику (интерфейс → реализация → внедрение через конструктор).
 *
 * @param <T>  тип сущности
 * @param <ID> тип идентификатора
 */
public interface Repository<T, ID> {

    /** Вставляет или заменяет сущность. Возвращает сохранённую. */
    T save(T entity);

    /** Ищет сущность по id, или {@link Optional#empty()}, если нет. */
    Optional<T> findById(ID id);

    /** Возвращает все хранимые сущности. */
    List<T> findAll();

    /**
     * Удаляет сущность с данным id.
     *
     * @return true, если реально что-то удалилось
     */
    boolean deleteById(ID id);

    /** Число хранимых сущностей. */
    long count();

    /** True, если сущность с таким id есть. */
    boolean existsById(ID id);

    /** Убирает всё (нужно тестам и «новой игре»). */
    void clear();
}
