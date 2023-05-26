package app.persistance.hibernate;

import app.model.Product;
import app.persistance.ProductRepository;
import app.persistance.utils.HibernateSession;
import app.persistance.utils.RepositoryException;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class HibernateProductRepository implements ProductRepository {

    @Override
    public Long save(Product entity) throws RepositoryException {
        try (Session session = HibernateSession.getInstance().openSession()) {
            session.beginTransaction();
            Long ID = (Long) session.save(entity);
            session.getTransaction().commit();
            return ID;
        } catch (Exception exception) {
            throw new RepositoryException("Error while saving product : " + exception.getMessage());
        }
    }

    @Override
    public Product delete(Long aLong) throws RepositoryException {
        try (Session session = HibernateSession.getInstance().openSession()) {
            session.beginTransaction();
            Product product = session.get(Product.class, aLong);
            session.delete(product);
            session.getTransaction().commit();
            return product;
        } catch (Exception ex) {
            throw new RepositoryException("Error while deleting product: " + ex.getMessage());
        }
    }

    @Override
    public Product findOne(Long aLong) throws RepositoryException {
        try (Session session = HibernateSession.getInstance().openSession()) {
            session.beginTransaction();
            Product product = session.get(Product.class, aLong);
            session.getTransaction().commit();
            return product;
        } catch (Exception ex) {
            throw new RepositoryException("Error while finding product: " + ex.getMessage());
        }
    }

    @Override
    public List<Product> findAll() {
        try (Session session = HibernateSession.getInstance().openSession()) {
            session.beginTransaction();
            List<Product> products = session.createQuery("FROM Product ").list();
            session.getTransaction().commit();
            return products;
        } catch (Exception exception) {
            System.err.println("Exception while finding all products!");
            return new ArrayList<>();
        }
    }

    @Override
    public void setNewQuantity(Long productId, Integer newQuantity) {
        try (Session session = HibernateSession.getInstance().openSession()) {
            session.beginTransaction();
            Product product = session.createQuery("FROM Product WHERE id = :product_id", Product.class)
                    .setParameter("product_id", productId)
                    .getSingleResult();
            product.setStock(newQuantity);
            session.merge(product);
            session.getTransaction().commit();
        } catch (Exception ex) {
            System.err.println("Error while setting new Quantity: " + ex.getMessage());
        }
    }

    @Override
    public void update(Product newProduct) throws RepositoryException {
        try(Session session = HibernateSession.getInstance().openSession()){
            session.beginTransaction();
            session.merge(newProduct);
            session.getTransaction().commit();
        }
        catch(Exception ex){
            System.err.println("Error while updating product! " + ex.getMessage());
            throw new RepositoryException(ex.getMessage());
        }
    }
}
