package shop.server.item.entity;

import jakarta.persistence.*;
import lombok.*;
import shop.server.basket.entity.Basket;
import shop.server.item.dto.ItemUpdateDto;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @Column(name = "item_Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(unique = true)
    private String name;

    private String company;

    private Integer price;

    private Integer stock;

    public void updateData(ItemUpdateDto updateDto) {
        this.name = updateDto.getName();
        this.company = updateDto.getCompany();
        this.price = updateDto.getPrice();
        this.stock = updateDto.getStock();
    }

    public void setStock(Integer value) {
        this.stock += value;
    }

}
