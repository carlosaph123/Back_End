package co.shop.luxury.repository;

import co.shop.luxury.model.Product;
import co.shop.luxury.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<ProductWrapper> getAllProducts();
}
