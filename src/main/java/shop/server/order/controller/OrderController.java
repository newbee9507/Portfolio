package shop.server.order.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.server.aop.annotation.TimeLog;
import shop.server.auth.memberdetails.MemberDetails;
import shop.server.order.dtos.OrderResponseDto;
import shop.server.order.dtos.OrderRegisterDto;
import shop.server.order.service.OrderService;

@RestController
@RequestMapping("/myShop/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @TimeLog
    @GetMapping("/info/{orderId}")
    public ResponseEntity<OrderResponseDto> getInfo(@AuthenticationPrincipal MemberDetails member,
                                                    @PathVariable Long orderId) {
        OrderResponseDto result = service.information(member.getMemberId(), orderId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @TimeLog
    @PostMapping("/register")
    public ResponseEntity<OrderResponseDto> saveOrder(@AuthenticationPrincipal MemberDetails member,
                                                 @RequestBody @Valid OrderRegisterDto dto) {
        OrderResponseDto result = service.saveOrder(member.getMemberId(), dto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @TimeLog
    @PatchMapping("/arriveOrder/{orderId}")
    public ResponseEntity<OrderResponseDto> arriveOrder(@AuthenticationPrincipal MemberDetails member,
                                                        @PathVariable @Positive Long orderId) {
        OrderResponseDto result = service.arriveOrder(orderId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @TimeLog
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<String> deleteOrder(@AuthenticationPrincipal MemberDetails member,
                                              @PathVariable @Positive Long orderId) {
        service.deleteOrder(member.getMemberId(), orderId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
