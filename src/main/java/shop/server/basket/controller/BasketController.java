package shop.server.basket.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.server.auth.memberdetails.MemberDetails;
import shop.server.basket.dto.BasketResponseDto;
import shop.server.basket.service.BasketService;

@RestController
@RequestMapping("/myShop/basket")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService service;

    @GetMapping("/info")
    public ResponseEntity<BasketResponseDto> infoBasket(@AuthenticationPrincipal MemberDetails member) {
        BasketResponseDto result = service.info(member.getMemberId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/add/{itemId}/{quantity}")
    public ResponseEntity<BasketResponseDto> inputBasket(@AuthenticationPrincipal MemberDetails member,
                              @PathVariable @Positive Long itemId,
                              @PathVariable @Positive Integer quantity) {
        BasketResponseDto result = service.input(member.getMemberId(), itemId, quantity);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping("/modifyQuantity/{itemId}/{quantity}")
    public ResponseEntity<BasketResponseDto> modifyQuantity(@AuthenticationPrincipal MemberDetails member,
                                                            @PathVariable @Positive Long itemId,
                                                            @PathVariable Integer quantity) {
        BasketResponseDto result = service.modifyQuantity(member.getMemberId(), itemId, quantity);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping("/clear")
    public ResponseEntity<BasketResponseDto> clearBasket(@AuthenticationPrincipal MemberDetails member) {
        BasketResponseDto result = service.clearBasket(member.getMemberId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<BasketResponseDto> deleteToBasket(@AuthenticationPrincipal MemberDetails member,
                                                            @PathVariable @Positive Long itemId) {
        BasketResponseDto result = service.removeToBasket(member.getMemberId(), itemId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

