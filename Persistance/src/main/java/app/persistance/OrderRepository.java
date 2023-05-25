package app.persistance;

import app.model.Order;
import app.model.Product;
import app.persistance.utils.RepositoryException;

public interface OrderRepository extends CRUDRepository<Long, Order> {

    boolean isProductPresentInAnyOrder(Product product) throws RepositoryException;

    void deleteOrdersThatContainsProduct(Product product) throws RepositoryException;

}
