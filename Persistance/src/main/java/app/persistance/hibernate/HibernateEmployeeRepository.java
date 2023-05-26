package app.persistance.hibernate;

import app.model.Employee;
import app.persistance.EmployeeRepository;
import app.persistance.utils.HibernateSession;
import app.persistance.utils.RepositoryException;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class HibernateEmployeeRepository implements EmployeeRepository {

    @Override
    public Long save(Employee entity) throws RepositoryException {
        try (Session session = HibernateSession.getInstance().openSession()) {
            session.beginTransaction();
            Long id = (Long) session.save(entity);
            session.getTransaction().commit();
            return id;
        } catch (Exception ex) {
            throw new RepositoryException(ex.getMessage());
        }
    }

    @Override
    public Employee delete(Long aLong) throws RepositoryException {
        return null;
    }

    @Override
    public Employee findOne(Long aLong) throws RepositoryException {
        try (Session session = HibernateSession.getInstance().openSession()) {
            session.beginTransaction();
            Employee employee = session.createQuery("FROM Employee WHERE id = :id", Employee.class)
                    .setParameter("id", aLong)
                    .getSingleResult();
            session.getTransaction().commit();
            return employee;
        } catch (Exception ex) {
            System.err.println("Error while finding one employee: " + ex.getMessage());
            throw new RepositoryException(ex.getMessage());
        }
    }

    @Override
    public List<Employee> findAll() {
        try (Session session = HibernateSession.getInstance().openSession()) {
            session.beginTransaction();
            List<Employee> result = new ArrayList<>(session.createQuery("FROM Employee", Employee.class).list());
            session.getTransaction().commit();
            return result;
        } catch (Exception ex) {
            System.err.println("Error while finding all employees: " + ex.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public Employee checkCredentials(String username, String password) throws RepositoryException {
        try (Session session = HibernateSession.getInstance().openSession()) {
            session.beginTransaction();
            Employee employee =
                    session.createQuery("FROM Employee WHERE username = :username AND password = :password", Employee.class)
                            .setParameter("username", username)
                            .setParameter("password", password)
                            .getSingleResult();
            session.getTransaction().commit();
            return employee;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            throw new RepositoryException("Error while checking credentials: " + ex.getMessage());
        }
    }
}
