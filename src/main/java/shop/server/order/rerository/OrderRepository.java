package shop.server.order.rerository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.server.order.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, DslOrderRepository {

    @Query("select o from orders o left join fetch o.itemList where o.id = :orderId")
    Order findByOrderIdFetchOrderItemList(@Param("orderId") Long orderId);

    @Modifying
    @Query("delete from OrderItem oi where oi.id in :ids")
    int deleteAllOrderItems(@Param("ids")List<Long> ids);
}
