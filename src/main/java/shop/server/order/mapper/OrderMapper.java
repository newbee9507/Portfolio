package shop.server.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import shop.server.basket.entity.BasketItem;
import shop.server.item.entity.Item;
import shop.server.order.dtos.OrderResponseDto;
import shop.server.order.dtos.OrderItemDto;
import shop.server.order.dtos.OrderRegisterDto;
import shop.server.order.entity.Order;
import shop.server.order.entity.OrderItem;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    default OrderResponseDto orderToOrderResponseDto(Order order) {
        OrderResponseDto result = new OrderResponseDto();

        result.setOrderId(order.getOrderId());
        result.setCustomerId(order.getClient().getId());
        result.setAddress(order.getAddress());
        result.setOrderTime(order.getOrderTime());

        String arrivalTime = Objects.isNull(order.getArrivalTime()) ? "Not Arrival" : order.getArrivalTime();
        result.setArrivalTime(arrivalTime);

        List<Long> itemIdList = order.getItemList().stream().map(OrderItem::getItem).map(Item::getItemId).toList();
        result.setItemList(itemIdList);
        return result;
    }

    default List<OrderItem> itemListToOrderItemList(List<Item> itemList, List<OrderItemDto> dtoList) {
        List<OrderItem> result = new ArrayList<>(itemList.size());

        for (int i = 0; i < itemList.size(); i++) {
            result.add(OrderItem.builder().item( itemList.get(i) )
                    .quantity( dtoList.get(i).getQuantity() ).build());
        }
        return result;

    }

    default Order orderRegisterDtoToOrder(OrderRegisterDto dto, int totalPrice) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        String orderTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);

        return Order.builder().client(null)
                .itemList(new ArrayList<>())
                .address(dto.getAddress())
                .totalPrice(totalPrice)
                .orderTime(orderTime)
                .arrivalTime(null)
                .build();
    }
}
