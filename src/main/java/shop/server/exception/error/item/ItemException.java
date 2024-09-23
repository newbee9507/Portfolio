package shop.server.exception.error.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import shop.server.exception.error.ErrorMessage;
import shop.server.exception.error.MyShopException;
import shop.server.item.entity.Item;

import java.util.ArrayList;
import java.util.List;

@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemException extends MyShopException {
    @Getter
    private List<Item> ErrorList;
    public ItemException(HttpStatus status, ErrorMessage message) {
        super(status, message);
        this.ErrorList = new ArrayList<>();
    }

    public ItemException(HttpStatus status, ErrorMessage message, List<Item> errorList) {
        super(status, message);
        ErrorList = errorList;
    }
}
