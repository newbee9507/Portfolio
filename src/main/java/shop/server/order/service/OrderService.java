package shop.server.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.server.exception.error.order.OrderExMessage;
import shop.server.exception.error.order.OrderException;
import shop.server.item.entity.Item;
import shop.server.item.service.ItemService;
import shop.server.member.entity.Member;
import shop.server.member.service.MemberService;
import shop.server.order.dtos.OrderResponseDto;
import shop.server.order.dtos.OrderItemDto;
import shop.server.order.dtos.OrderRegisterDto;
import shop.server.order.entity.Order;
import shop.server.order.mapper.OrderMapper;
import shop.server.order.rerository.OrderRepository;
import shop.server.order.entity.OrderItem;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper mapper;
    private final OrderRepository repository;
    private final MemberService memberService;
    private final ItemService itemService;

    @Transactional(readOnly = true)
    public OrderResponseDto information(Long memberId, Long orderId) {
        Member customer = memberService.findByMemberIdFetchOrderList(memberId);
        Order order = repository.getReferenceById(orderId);
        if (Objects.isNull(customer) ||!customer.getOrderList().contains(order)) {
            throw new OrderException(HttpStatus.UNAUTHORIZED, OrderExMessage.UNAUTHORIZED);
        }
        order.getItemList(); // 초기화
        return mapper.orderToOrderResponseDto(order);
    }

    public OrderResponseDto saveOrder(Long memberId, OrderRegisterDto registerDto) {
        Member member = memberService.findByMemberId(memberId);

        List<OrderItemDto> dtoList = registerDto.getDtoList();
        dtoList.sort(Comparator.comparing(OrderItemDto::getItemId));

        List<Long> itemIdList = dtoList.stream()
                .map(OrderItemDto::getItemId).toList();
        List<Item> itemList = itemService.findByIds(itemIdList);

        int totalPrice = 0;
        for (int i = 0; i < dtoList.size(); i++) {
            totalPrice += itemList.get(i).getPrice() * dtoList.get(i).getQuantity();
        }

        List<OrderItem> orderItems = mapper.itemListToOrderItemList(itemList, dtoList);
        orderItems.forEach(orderItem -> itemService.checkStock(orderItem.getItem(), orderItem.getQuantity()));

        memberService.checkPoint(member, totalPrice);
        actualPayment(member, itemList, dtoList, totalPrice);

        Order newOrder = mapper.orderRegisterDtoToOrder(registerDto, totalPrice);
        newOrder.mappringMember(member);
        orderItems.forEach(orderItem -> orderItem.mappingOrder(newOrder));

        repository.save(newOrder);
        return mapper.orderToOrderResponseDto(newOrder);
    }

    public boolean actualPayment(Member member, List<Item> itemList, List<OrderItemDto> dtoList, int totalPrice) {

        for (int i = 0; i < itemList.size(); i++) {
            itemList.get(i).setStock(dtoList.get(i).getQuantity() * -1);
        }
        member.addPoint(totalPrice * -1);
        return true;
    }

    public OrderResponseDto arriveOrder(Long orderId) {
        Order order = findByOrderId(orderId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        String arriveTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
        order.setArrivalTime(arriveTime);

        return mapper.orderToOrderResponseDto(order);
    }

    public String deleteOrder(Long memberId, Long orderId) {
        Member customer = memberService.findByMemberIdFetchOrderList(memberId);
        Order order = repository.getReferenceById(orderId);
        if (Objects.isNull(customer) ||!customer.getOrderList().contains(order)) {
            throw new OrderException(HttpStatus.UNAUTHORIZED, OrderExMessage.UNAUTHORIZED);
        }
        List<Long> orderItemIds = repository.findOrderItemIds(orderId);
        repository.deleteAllOrderItems(orderItemIds);
        customer.getOrderList().remove(order);
        return "OK";
    }

    public Order findByOrderId(Long orderId) {
        return repository.findById(orderId).orElseThrow(
                () -> new OrderException(HttpStatus.NOT_FOUND, OrderExMessage.NOT_EXIST));
    }
}
