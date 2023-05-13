package app.persistance;

import app.model.Employee;
import app.persistance.utils.RepositoryException;

public interface EmployeeRepository extends CRUDRepository<Long, Employee> {
    Employee checkCredentials(String username, String password) throws RepositoryException;
}
