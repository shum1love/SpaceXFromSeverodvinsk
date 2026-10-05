package org.example.spacecompany.repository;

import java.util.List;
import java.util.UUID;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketStatus;

/** Контракт хранения ракет плюс предметные поисковики. */
public interface RocketRepository extends Repository<Rocket, UUID> {

    /** All rockets currently in the given status. */
    List<Rocket> findByStatus(RocketStatus status);

    /** Rockets whose name contains the given text (case-insensitive). */
    List<Rocket> findByNameContaining(String text);
}
