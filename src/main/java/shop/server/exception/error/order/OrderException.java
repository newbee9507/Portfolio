package shop.server.exception.error.order;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import shop.server.exception.error.ErrorMessage;
import shop.server.exception.error.MyShopException;

@ToString
public class OrderException extends MyShopException {

    public OrderException(HttpStatus status, ErrorMessage message) {
        super(status, message);
    }
}
