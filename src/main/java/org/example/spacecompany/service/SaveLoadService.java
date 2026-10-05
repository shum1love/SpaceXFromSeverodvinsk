package org.example.spacecompany.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.example.spacecompany.domain.Company;
import org.example.spacecompany.domain.LaunchRecord;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.RocketStatus;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.EmployeeRole;
import org.example.spacecompany.domain.employee.EmployeeStatus;
import org.example.spacecompany.domain.employee.Engineer;
import org.example.spacecompany.domain.employee.MissionSpecialist;
import org.example.spacecompany.domain.employee.Pilot;
import org.example.spacecompany.domain.employee.Scientist;
import org.example.spacecompany.domain.finance.Transaction;
import org.example.spacecompany.domain.finance.TransactionType;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionStatus;
import org.example.spacecompany.domain.mission.MissionType;
import org.example.spacecompany.exception.GameSaveException;
import org.example.spacecompany.repository.EmployeeRepository;
import org.example.spacecompany.repository.InMemoryEmployeeRepository;
import org.example.spacecompany.repository.InMemoryMissionRepository;
import org.example.spacecompany.repository.InMemoryRocketRepository;
import org.example.spacecompany.repository.MissionRepository;
import org.example.spacecompany.repository.RocketRepository;

/**
 * Сохраняет и загружает всю игру в человекочитаемый текстовый файл.
 *
 * <p>Только {@code java.nio.file} ({@link Path}, {@link Files}) плюс
 * try-with-resources. Формат нарочно простой — секции с полями через {@code |},
 * сейв можно открыть (и даже поправить руками) любым текстовым редактором:
 *
 * <pre>
 * # SPACE COMPANY SAVE v1
 * [COMPANY]
 * name=Северодвинские Звёзды
 * ...
 * [ROCKETS]
 * &lt;uuid&gt;|&lt;имя&gt;|STARSHIP|500|100|READY|3|2
 * ...
 * </pre>
 *
 * <p>Поля экранируют {@code \}, {@code |} и переводы строк бэкслэшем, поэтому
 * вольный текст (названия, журналы) не ломает структуру.
 */
public class SaveLoadService {

    private static final String HEADER = "# SPACE COMPANY SAVE v1";

    // ------------------------------------------------------------------
    // Save
    // ------------------------------------------------------------------

    /** Writes the company state to {@code file} (overwrites existing). */
    public void save(Company company, Path file) throws GameSaveException {
        Objects.requireNonNull(company, "company");
        Objects.requireNonNull(file, "file");
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(HEADER);
            writer.newLine();

            writer.write("[COMPANY]");
            writer.newLine();
            writer.write("name=" + escape(company.getName()));
            writer.newLine();
            writer.write("balance=" + company.getBalance());
            writer.newLine();
            writer.write("founded=" + company.getFoundedDate());
            writer.newLine();

            writer.write("[ROCKETS]");
            writer.newLine();
            for (Rocket rocket : company.getRockets().findAll()) {
                writer.write(String.join("|",
                        rocket.getId().toString(),
                        escape(rocket.getName()),
                        rocket.getModel().name(),
                        String.valueOf(rocket.getFuel()),
                        String.valueOf(rocket.getCondition()),
                        rocket.getStatus().name(),
                        String.valueOf(rocket.getLaunchCount()),
                        String.valueOf(rocket.getSuccessCount())));
                writer.newLine();
            }

            writer.write("[EMPLOYEES]");
            writer.newLine();
            for (Employee employee : company.getEmployees().findAll()) {
                writer.write(String.join("|",
                        employee.getId().toString(),
                        employee.getRole().name(),
                        escape(employee.getName()),
                        employee.getSalary().toPlainString(),
                        String.valueOf(employee.getExperienceYears()),
                        String.valueOf(employee.getSkillLevel()),
                        employee.getStatus().name(),
                        employee.getHireDate().toString()));
                writer.newLine();
            }

            writer.write("[MISSIONS]");
            writer.newLine();
            for (Mission mission : company.getMissions().findAll()) {
                StringBuilder crew = new StringBuilder();
                for (UUID id : mission.getCrewIds()) {
                    if (!crew.isEmpty()) {
                        crew.append(',');
                    }
                    crew.append(id);
                }
                writer.write(String.join("|",
                        mission.getId().toString(),
                        escape(mission.getName()),
                        mission.getType().name(),
                        String.valueOf(mission.getRequiredPayloadKg()),
                        mission.getReward().toPlainString(),
                        mission.getStatus().name(),
                        mission.getAssignedRocketId() == null ? "" : mission.getAssignedRocketId().toString(),
                        crew.toString(),
                        String.valueOf(mission.getSuccessProbability()),
                        mission.getCreatedAt().toString(),
                        mission.getLaunchedAt() == null ? "" : mission.getLaunchedAt().toString(),
                        mission.getCompletedAt() == null ? "" : mission.getCompletedAt().toString(),
                        escape(mission.getResultLog() == null ? "" : mission.getResultLog())));
                writer.newLine();
            }

            writer.write("[TRANSACTIONS]");
            writer.newLine();
            for (Transaction transaction : company.getTransactions()) {
                writer.write(String.join("|",
                        transaction.getId().toString(),
                        transaction.getType().name(),
                        transaction.getAmount().toPlainString(),
                        escape(transaction.getDescription()),
                        transaction.getTimestamp().toString()));
                writer.newLine();
            }

            writer.write("[HISTORY]");
            writer.newLine();
            for (LaunchRecord record : company.getLaunchHistory()) {
                writer.write(String.join("|",
                        record.getId().toString(),
                        escape(record.getMissionName()),
                        escape(record.getRocketName()),
                        String.valueOf(record.isSuccess()),
                        record.getNetProfit().toPlainString(),
                        record.getTimestamp().toString()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new GameSaveException("Не получается сохранить игру в " + file + ": " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    // Load
    // ------------------------------------------------------------------

    /** Reads a save file back into a fully wired {@link Company}. */
    public Company load(Path file) throws GameSaveException {
        Objects.requireNonNull(file, "file");
        final List<String> lines;
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            lines = reader.lines().toList();
        } catch (IOException e) {
            throw new GameSaveException("Не получается загрузить игру из " + file + ": " + e.getMessage(), e);
        }
        try {
            return parse(lines);
        } catch (IllegalArgumentException e) {
            throw new GameSaveException("Файл сохранения повреждён: " + e.getMessage(), null);
        }
    }

    private Company parse(List<String> lines) {
        if (lines.isEmpty() || !lines.get(0).equals(HEADER)) {
            throw new IllegalArgumentException("missing header '" + HEADER + "'");
        }
        Map<String, List<String>> sections = new LinkedHashMap<>();
        String current = null;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("[") && line.endsWith("]")) {
                current = line;
                sections.put(current, new ArrayList<>());
            } else if (current != null && !line.isBlank()) {
                sections.get(current).add(line);
            }
        }

        Map<String, String> companyProps = new LinkedHashMap<>();
        for (String line : sections.getOrDefault("[COMPANY]", List.of())) {
            int eq = line.indexOf('=');
            if (eq < 0) {
                throw new IllegalArgumentException("bad company line: " + line);
            }
            companyProps.put(line.substring(0, eq), unescape(line.substring(eq + 1)));
        }
        String name = companyProps.get("name");
        String balanceText = companyProps.get("balance");
        String foundedText = companyProps.get("founded");
        if (name == null || balanceText == null || foundedText == null) {
            throw new IllegalArgumentException("incomplete [COMPANY] section");
        }

        RocketRepository rockets = new InMemoryRocketRepository();
        for (String line : sections.getOrDefault("[ROCKETS]", List.of())) {
            List<String> f = split(line);
            if (f.size() != 8) {
                throw new IllegalArgumentException("bad rocket line: " + line);
            }
            rockets.save(new Rocket(UUID.fromString(f.get(0)), unescape(f.get(1)),
                    RocketModel.valueOf(f.get(2)), Integer.parseInt(f.get(3)),
                    Integer.parseInt(f.get(4)), RocketStatus.valueOf(f.get(5)),
                    Integer.parseInt(f.get(6)), Integer.parseInt(f.get(7))));
        }

        EmployeeRepository employees = new InMemoryEmployeeRepository();
        for (String line : sections.getOrDefault("[EMPLOYEES]", List.of())) {
            List<String> f = split(line);
            if (f.size() != 8) {
                throw new IllegalArgumentException("bad employee line: " + line);
            }
            employees.save(createEmployee(
                    UUID.fromString(f.get(0)), EmployeeRole.valueOf(f.get(1)), unescape(f.get(2)),
                    new BigDecimal(f.get(3)), Integer.parseInt(f.get(4)), Integer.parseInt(f.get(5)),
                    EmployeeStatus.valueOf(f.get(6)), LocalDate.parse(f.get(7))));
        }

        MissionRepository missions = new InMemoryMissionRepository();
        for (String line : sections.getOrDefault("[MISSIONS]", List.of())) {
            List<String> f = split(line);
            if (f.size() != 13) {
                throw new IllegalArgumentException("bad mission line: " + line);
            }
            Mission mission = new Mission(UUID.fromString(f.get(0)), unescape(f.get(1)),
                    MissionType.valueOf(f.get(2)), Integer.parseInt(f.get(3)),
                    new BigDecimal(f.get(4)), LocalDateTime.parse(f.get(9)));
            mission.setStatus(MissionStatus.valueOf(f.get(5)));
            if (!f.get(6).isEmpty()) {
                mission.assignRocket(UUID.fromString(f.get(6)));
            }
            if (!f.get(7).isEmpty()) {
                Set<UUID> crew = new LinkedHashSet<>();
                for (String id : f.get(7).split(",")) {
                    crew.add(UUID.fromString(id));
                }
                mission.restoreCrew(crew);
            }
            mission.setSuccessProbability(Double.parseDouble(f.get(8)));
            if (!f.get(10).isEmpty()) {
                mission.setLaunchedAt(LocalDateTime.parse(f.get(10)));
            }
            if (!f.get(11).isEmpty()) {
                mission.setCompletedAt(LocalDateTime.parse(f.get(11)));
            }
            String resultLog = unescape(f.get(12));
            if (!resultLog.isEmpty()) {
                mission.setResultLog(resultLog);
            }
            missions.save(mission);
        }

        Company company = new Company(name, new BigDecimal(balanceText),
                rockets, employees, missions, LocalDate.parse(foundedText));

        List<Transaction> transactions = new ArrayList<>();
        for (String line : sections.getOrDefault("[TRANSACTIONS]", List.of())) {
            List<String> f = split(line);
            if (f.size() != 5) {
                throw new IllegalArgumentException("bad transaction line: " + line);
            }
            transactions.add(new Transaction(UUID.fromString(f.get(0)),
                    TransactionType.valueOf(f.get(1)), new BigDecimal(f.get(2)),
                    unescape(f.get(3)), LocalDateTime.parse(f.get(4))));
        }
        company.restoreTransactions(transactions);

        List<LaunchRecord> history = new ArrayList<>();
        for (String line : sections.getOrDefault("[HISTORY]", List.of())) {
            List<String> f = split(line);
            if (f.size() != 6) {
                throw new IllegalArgumentException("bad history line: " + line);
            }
            history.add(new LaunchRecord(UUID.fromString(f.get(0)), unescape(f.get(1)),
                    unescape(f.get(2)), Boolean.parseBoolean(f.get(3)),
                    new BigDecimal(f.get(4)), LocalDateTime.parse(f.get(5))));
        }
        company.restoreLaunchHistory(history);

        return company;
    }

    private static Employee createEmployee(UUID id, EmployeeRole role, String name,
                                           BigDecimal salary, int experience, int skill,
                                           EmployeeStatus status, LocalDate hireDate) {
        Employee employee = switch (role) {
            case ENGINEER -> new Engineer(id, name, salary, experience, skill, hireDate);
            case PILOT -> new Pilot(id, name, salary, experience, skill, hireDate);
            case SCIENTIST -> new Scientist(id, name, salary, experience, skill, hireDate);
            case MISSION_SPECIALIST -> new MissionSpecialist(id, name, salary, experience, skill, hireDate);
        };
        employee.setStatus(status);
        return employee;
    }

    // ------------------------------------------------------------------
    // Escaping helpers
    // ------------------------------------------------------------------

    /** Escapes {@code \}, {@code |} and line breaks. */
    static String escape(String text) {
        StringBuilder sb = new StringBuilder(text.length() + 16);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '|' -> sb.append("\\p");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Inverse of {@link #escape}. */
    static String unescape(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\' && i + 1 < text.length()) {
                char next = text.charAt(++i);
                switch (next) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 'p' -> sb.append('|');
                    case '\\' -> sb.append('\\');
                    default -> sb.append(next);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Splits on unescaped {@code |} (escape sequences stay intact). */
    static List<String> split(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\\' && i + 1 < line.length()) {
                current.append(c).append(line.charAt(++i));
            } else if (c == '|') {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        parts.add(current.toString());
        return parts;
    }
}
