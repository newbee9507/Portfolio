package shop.server.exception.error.basket;

import org.springframework.http.HttpStatus;
import shop.server.exception.error.ErrorMessage;
import shop.server.exception.error.MyShopException;

public class BasketException extends MyShopException {

    public BasketException(HttpStatus status, ErrorMessage message) {
        super(status, message);
    }
}
