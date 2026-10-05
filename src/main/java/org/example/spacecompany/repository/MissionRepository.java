package org.example.spacecompany.repository;

import java.util.List;
import java.util.UUID;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionStatus;

/** Контракт хранения миссий плюс предметные поисковики. */
public interface MissionRepository extends Repository<Mission, UUID> {

    /** All missions currently in the given status. */
    List<Mission> findByStatus(MissionStatus status);
}
