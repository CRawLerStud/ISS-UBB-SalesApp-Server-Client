package app.persistance;

import app.model.Product;
import app.persistance.utils.RepositoryException;

public interface ProductRepository extends CRUDRepository<Long, Product> {

    void setNewQuantity(Long productId, Integer newQuantity);

    void update(Product newProduct) throws RepositoryException;
}
