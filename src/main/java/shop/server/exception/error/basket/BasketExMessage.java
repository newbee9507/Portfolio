package shop.server.exception.error.basket;

import lombok.Getter;
import shop.server.exception.error.ErrorMessage;

@Getter
public enum BasketExMessage implements ErrorMessage {

    NOT_EXIST("장바구니에 존재하지 않는 상품입니다"),;

    private final String message;

    BasketExMessage(String message) {
        this.message = message;
    }
}
