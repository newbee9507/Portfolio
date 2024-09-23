package shop.server.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class MyShopException extends RuntimeException{

    @Getter
    private final HttpStatus status;
    private final ErrorMessage message;

    public String getMessage() {
        return this.message.getMessage();
    }
}
