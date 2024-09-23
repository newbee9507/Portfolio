package shop.server.item.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ItemResponseDto {

    private Long itemId;

    private String name;

    private String company;

    private Integer price;

    private Integer stock;
}
