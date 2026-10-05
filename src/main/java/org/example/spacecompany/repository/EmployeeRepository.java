package org.example.spacecompany.repository;

import java.util.List;
import java.util.UUID;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.EmployeeRole;

/** Контракт хранения сотрудников плюс предметные поисковики. */
public interface EmployeeRepository extends Repository<Employee, UUID> {

    /** All employees with the given role. */
    List<Employee> findByRole(EmployeeRole role);

    /** Employees whose status allows mission assignment. */
    List<Employee> findAvailable();
}
