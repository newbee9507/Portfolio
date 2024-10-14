package shop.server.basket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import shop.server.basket.dto.BasketResponseDto;
import shop.server.basket.entity.Basket;
import shop.server.basket.dto.BasketItemResponseDto;
import shop.server.basket.entity.BasketItem;
import shop.server.item.entity.Item;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BasketMapper {
    @Mapping(source = "basketItems", target = "basketItems")
    BasketResponseDto basketToBasketResponseDto(Basket basket, List<BasketItemResponseDto> basketItems);

    @Mapping(source = "item", target = "item")
    BasketItem itemToBasketItem(Item item);

    default BasketItemResponseDto basketItemToBasketItemResponseDto(BasketItem basketItem) {
        BasketItemResponseDto result = new BasketItemResponseDto();

        result.setItemId(basketItem.getItem().getItemId());
        result.setQuantity(basketItem.getQuantity());
        return result;
    }

    default List<BasketItemResponseDto> basketToBasketItemResponseDtoList(Basket basket) {

        return basket.getBasketItems().stream().map(this::basketItemToBasketItemResponseDto).toList();
    }
}
