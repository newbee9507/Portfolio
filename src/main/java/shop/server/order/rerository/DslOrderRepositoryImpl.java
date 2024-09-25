package shop.server.order.rerository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import shop.server.order.entity.Order;
import shop.server.order.entity.OrderItem;

import java.util.List;

import static shop.server.order.entity.QOrder.order;
import static shop.server.order.entity.QOrderItem.orderItem;

@RequiredArgsConstructor
public class DslOrderRepositoryImpl implements DslOrderRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteOrder(Order target) {
        List<OrderItem> targetList = target.getItemList();
        queryFactory.delete(orderItem).where(orderItem.in(targetList)).execute();
        queryFactory.delete(order).where(order.eq(target)).execute();
    }
}
