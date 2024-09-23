package shop.server.exception.error.order;

import lombok.Getter;
import shop.server.exception.error.ErrorMessage;

@Getter
public enum OrderExMessage implements ErrorMessage {

    NOT_EXIST("존재하지 않는 주문번호입니다."),
    UNAUTHORIZED("본인의 주문내역만 조회 가능합니다"),
    ;

    private final String message;

    OrderExMessage(String message) {
        this.message = message;
    }
}
