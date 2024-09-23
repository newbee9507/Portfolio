package shop.server.basket.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BasketResponseDto {

    private Long basketId;

    private List<BasketItemResponseDto> basketItems;
}
