package app.persistance;

import app.model.Product;

public interface ProductRepository extends CRUDRepository<Long, Product> {

    public void sellQuantity(Long productId, Integer newQuantity);

}
