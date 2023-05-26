package app.persistance.hibernate;

import app.model.Order;
import app.model.Product;
import app.persistance.OrderRepository;
import app.persistance.utils.HibernateSession;
import app.persistance.utils.RepositoryException;
import org.hibernate.Session;

import java.util.List;

public class HibernateOrderRepository implements OrderRepository {

    @Override
    public Long save(Order entity) throws RepositoryException {
        try(Session session = HibernateSession.getInstance().openSession()){
            session.beginTransaction();
            Long ID = (Long) session.save(entity);
            session.getTransaction().commit();
            return ID;
        }
        catch(Exception ex){
            throw new RepositoryException("Error while saving order: " + ex.getMessage());
        }
    }

    @Override
    public Order delete(Long aLong) throws RepositoryException {
        return null;
    }

    @Override
    public Order findOne(Long aLong) throws RepositoryException {
        return null;
    }

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public boolean isProductPresentInAnyOrder(Product product) throws RepositoryException {
        try(Session session = HibernateSession.getInstance().openSession()){
            session.beginTransaction();
            Long noOfAparitions = (Long) session.createQuery("SELECT COUNT(*) FROM OrderEntry OE " +
                    "INNER JOIN Product P ON OE.product.id = :product_id")
                    .setParameter("product_id", product.getId())
                    .uniqueResult();
            session.getTransaction().commit();
            return noOfAparitions > 0;
        }
        catch(Exception ex){
            throw new RepositoryException("Error while isProductPresentInAnyOrder: " + ex.getMessage());
        }
    }

    @Override
    public void deleteOrdersThatContainsProduct(Product product) throws RepositoryException {
        try(Session session = HibernateSession.getInstance().openSession()){
            session.beginTransaction();
            session.createQuery("DELETE FROM OrderEntry OE " +
                    "WHERE OE.product.id = :product_id")
                    .setParameter("product_id", product.getId())
                    .executeUpdate();
            session.getTransaction().commit();
        }
        catch(Exception ex){
            throw new RepositoryException("Exception while deleting Orders That Contains Product:" + ex.getMessage());
        }
    }
}
