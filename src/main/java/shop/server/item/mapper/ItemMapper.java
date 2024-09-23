package shop.server.item.mapper;

import org.mapstruct.*;
import org.springframework.util.StringUtils;
import shop.server.item.dto.ItemRegistrationDto;
import shop.server.item.dto.ItemResponseDto;
import shop.server.item.dto.ItemUpdateDto;
import shop.server.item.entity.Item;

import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    Item ItemRegistrationDtoToItem(ItemRegistrationDto registrationDto);

    ItemResponseDto ItemToItemResponseDto(Item item);

    default ItemUpdateDto injectionValueIfNull(Item item, ItemUpdateDto dto) {
        String name = StringUtils.hasText(dto.getName()) ? dto.getName() : item.getName();
        String company = StringUtils.hasText(dto.getCompany()) ? dto.getCompany() : item.getCompany();
        Integer price = Objects.isNull(dto.getPrice()) ? item.getPrice() : dto.getPrice();
        Integer stock = Objects.isNull(dto.getStock()) ? item.getStock() : dto.getStock();

        return new ItemUpdateDto(name, company, price, stock);
    }
}
