package app.persistance.hibernate;

import app.model.Client;
import app.persistance.ClientRepository;
import app.persistance.utils.HibernateSession;
import app.persistance.utils.RepositoryException;
import org.hibernate.Session;

import java.util.List;

public class HibernateClientRepository implements ClientRepository {

    @Override
    public Long save(Client entity) throws RepositoryException {
        try(Session session = HibernateSession.getInstance().openSession()){
            session.beginTransaction();
            Long ID = (Long) session.save(entity);
            session.getTransaction().commit();
            return ID;
        }
        catch(Exception ex){
            System.err.println("Exception while saving client: " + ex.getMessage());
            throw new RepositoryException(ex.getMessage());
        }
    }

    @Override
    public Client delete(Long aLong) throws RepositoryException {
        return null;
    }

    @Override
    public Client findOne(Long aLong) throws RepositoryException {
        return null;
    }

    @Override
    public List<Client> findAll() {
        return null;
    }
}
