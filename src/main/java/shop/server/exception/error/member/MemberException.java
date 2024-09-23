package shop.server.exception.error.member;

import lombok.ToString;
import org.springframework.http.HttpStatus;
import shop.server.exception.error.ErrorMessage;
import shop.server.exception.error.MyShopException;

@ToString
public class MemberException extends MyShopException {

    public MemberException(HttpStatus status, ErrorMessage message) {
        super(status, message);
    }
}
