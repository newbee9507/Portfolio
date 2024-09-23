package shop.server.basket.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import shop.server.item.entity.Item;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"basketItemId", "basket", "quantity"})
public class BasketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "basketItem_Id")
    private Long basketItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basket_Id")
    private Basket basket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_Id")
    private Item item;

    private Integer quantity;

    public Integer modifyQuantity(Integer quantity) {
        this.quantity += quantity;
        return this.quantity;
    }

    public void mappingItem(Item item, Integer quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public void mappingToBasket(Basket basket) {
        this.basket = basket;
        basket.getBasketItems().add(this);
    }
}
