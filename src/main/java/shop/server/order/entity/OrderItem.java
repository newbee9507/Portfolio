package shop.server.order.entity;

import jakarta.persistence.*;
import lombok.*;
import shop.server.item.entity.Item;
import shop.server.order.entity.Order;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderItem_Id")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_Id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_Id")
    private Item item;

    private Integer quantity;

    public void mappingOrder(Order order) {
        this.order = order;
        order.getItemList().add(this);
    }
}
