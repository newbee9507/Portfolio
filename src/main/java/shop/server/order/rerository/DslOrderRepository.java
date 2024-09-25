package shop.server.order.rerository;

import shop.server.order.entity.Order;

public interface DslOrderRepository {

    void deleteOrder(Order order);
}
