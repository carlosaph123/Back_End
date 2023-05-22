package co.shop.luxury.repository;

import co.shop.luxury.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> getAllOrders();

    List<Order> getOrderByUsername(@Param("username") String username);
}
