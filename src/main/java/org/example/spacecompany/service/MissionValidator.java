package org.example.spacecompany.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketStatus;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.EmployeeRole;
import org.example.spacecompany.domain.employee.EmployeeStatus;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionStatus;
import org.example.spacecompany.domain.mission.MissionType;
import org.example.spacecompany.exception.MissionValidationException;

/**
 * Предстартовые проверки. Статические методы, без состояния: проверка — чистая
 * функция от (миссия, ракета, экипаж), которая либо молча проходит, либо бросает
 * {@link MissionValidationException} с человеческой причиной.
 */
public final class MissionValidator {

    /**
     * Roles required per mission kind. A {@link EmployeeRole#MISSION_SPECIALIST}
     * in the crew can cover any single missing role (wildcard).
     */
    private static final Map<MissionType, Set<EmployeeRole>> REQUIRED_ROLES = new EnumMap<>(MissionType.class);

    static {
        REQUIRED_ROLES.put(MissionType.SATELLITE_LAUNCH, EnumSet.of(EmployeeRole.PILOT));
        REQUIRED_ROLES.put(MissionType.CARGO_DELIVERY, EnumSet.of(EmployeeRole.PILOT, EmployeeRole.ENGINEER));
        REQUIRED_ROLES.put(MissionType.RESEARCH, EnumSet.of(EmployeeRole.PILOT, EmployeeRole.SCIENTIST));
        REQUIRED_ROLES.put(MissionType.MOON_MISSION,
                EnumSet.of(EmployeeRole.PILOT, EmployeeRole.ENGINEER, EmployeeRole.SCIENTIST));
        REQUIRED_ROLES.put(MissionType.MARS_MISSION,
                EnumSet.of(EmployeeRole.PILOT, EmployeeRole.ENGINEER, EmployeeRole.SCIENTIST));
    }

    private MissionValidator() {
    }

    /** Roles required for the given mission kind (unmodifiable copy). */
    public static Set<EmployeeRole> requiredRolesFor(MissionType type) {
        return Set.copyOf(REQUIRED_ROLES.getOrDefault(type, Set.of()));
    }

    /**
     * Validates everything needed for a launch.
     *
     * @throws MissionValidationException on the first problem found
     */
    public static void validateForLaunch(Mission mission, Rocket rocket, List<Employee> crew) {
        Objects.requireNonNull(mission, "mission");
        Objects.requireNonNull(rocket, "rocket");
        Objects.requireNonNull(crew, "crew");

        if (mission.getStatus() == MissionStatus.IN_PROGRESS) {
            throw new MissionValidationException("Миссия «" + mission.getName() + "» уже летит");
        }
        if (mission.getStatus().isTerminal()) {
            throw new MissionValidationException(
                    "Миссия «" + mission.getName() + "» уже завершена со статусом " + mission.getStatus());
        }
        if (mission.getStatus() == MissionStatus.CANCELLED) {
            throw new MissionValidationException("Миссия «" + mission.getName() + "» была отменена");
        }

        switch (rocket.getStatus()) {
            case DESTROYED -> throw new MissionValidationException(
                    "Ракета «" + rocket.getName() + "» уничтожена");
            case IN_MAINTENANCE -> throw new MissionValidationException(
                    "Ракета «" + rocket.getName() + "» на техобслуживании");
            case DAMAGED -> throw new MissionValidationException(
                    "Ракета «" + rocket.getName() + "» повреждена — нужен ремонт");
            case ON_MISSION -> throw new MissionValidationException(
                    "Ракета «" + rocket.getName() + "» уже на миссии");
            case NO_FUEL -> throw new MissionValidationException(
                    "Ракета «" + rocket.getName() + "» без топлива — заправьте её");
            case READY -> {
                // единственное состояние, в котором можно лететь; делать нечего
            }
        }

        if (rocket.getFuel() < rocket.getMaxFuel()) {
            throw new MissionValidationException(
                    "Бак ракеты «" + rocket.getName() + "» не полон");
        }
        if (rocket.getPayloadCapacityKg() < mission.getRequiredPayloadKg()) {
            throw new MissionValidationException(
                    "Грузоподъёмность ракеты %d кг меньше нужных %d кг"
                            .formatted(rocket.getPayloadCapacityKg(), mission.getRequiredPayloadKg()));
        }

        if (crew.size() < mission.getType().getMinCrewSize()) {
            throw new MissionValidationException(
                    "Миссии нужно минимум %d чел. экипажа, назначено только %d"
                            .formatted(mission.getType().getMinCrewSize(), crew.size()));
        }
        for (Employee member : crew) {
            if (member.getStatus() != EmployeeStatus.AVAILABLE) {
                throw new MissionValidationException(
                        "Член экипажа «" + member.getName() + "» недоступен (" + member.getStatus() + ")");
            }
        }
        validateRoles(mission.getType(), crew);

        if (rocket.getStatus() == RocketStatus.DESTROYED) {
            throw new MissionValidationException("Ракета уничтожена");
        }
    }

    private static void validateRoles(MissionType type, List<Employee> crew) {
        Set<EmployeeRole> required = REQUIRED_ROLES.getOrDefault(type, Set.of());
        if (required.isEmpty()) {
            return;
        }
        Set<EmployeeRole> present = EnumSet.noneOf(EmployeeRole.class);
        int specialists = 0;
        for (Employee member : crew) {
            if (member.getRole() == EmployeeRole.MISSION_SPECIALIST) {
                specialists++;
            } else {
                present.add(member.getRole());
            }
        }
        List<EmployeeRole> missing = new ArrayList<>();
        for (EmployeeRole role : required) {
            if (!present.contains(role)) {
                missing.add(role);
            }
        }
        // Each specialist covers one missing role.
        int uncovered = Math.max(0, missing.size() - specialists);
        if (uncovered > 0) {
            List<EmployeeRole> stillMissing = missing.subList(specialists, missing.size());
            throw new MissionValidationException(
                    "В экипаже не хватает ролей: " + stillMissing + " для «" + type.getDisplayName() + "»");
        }
    }
}
