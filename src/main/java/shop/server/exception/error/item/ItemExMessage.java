package shop.server.exception.error.item;

import lombok.Getter;
import shop.server.exception.error.ErrorMessage;

@Getter
public enum ItemExMessage implements ErrorMessage {
    ALREADY_EXIST("이미 등록된 상품입니다"),
    NOT_EXIST("등록되지 않은 상품입니다"),
    NOT_EXIST_CONDITION("조건과 일치하는 상품이 존재하지 않습니다"),
    PLEASE_INSERT_DATA("상품의 이름, 제조사, 가격, 재고수량들 중, 하나 이상은 입력해야만 합니다."),
    NOT_ENOUGH_STOCK("상품의 재고가 부족합니다. 빠른 시일내에 재고를 확보하겠습니다."),
    ;

    private final String message;

    ItemExMessage(String message) {
        this.message = message;
    }
}
