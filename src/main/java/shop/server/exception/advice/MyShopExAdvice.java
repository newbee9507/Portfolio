package shop.server.exception.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.server.exception.error.ErrorResponse;
import shop.server.exception.error.MyShopException;
import shop.server.exception.error.basket.BasketExMessage;
import shop.server.exception.error.basket.BasketException;
import shop.server.exception.error.item.ItemException;
import shop.server.exception.error.member.MemberException;
import shop.server.exception.error.order.OrderException;
import shop.server.item.entity.Item;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class MyShopExAdvice {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> myShopExHandler(MemberException mE) {
        log.info("myShopExHandler");
        ErrorResponse errorResponse = ErrorResponse.buildErrorResponse(mE);
        return new ResponseEntity<>(errorResponse, mE.getStatus());
    }

    @ExceptionHandler(ItemException.class)
    public ResponseEntity<ErrorResponse> itemExHandler(ItemException iE) {
        log.info("itemExHandler");
        ErrorResponse errorResponse = ErrorResponse.buildErrorResponse(iE);
        List<Item> errorItems = iE.getErrorList();
        if (!errorItems.isEmpty()) {
            for (Item errorItem : errorItems) {
                errorResponse.setFieldErrors(errorItem.getName(), errorItem.getStock() + "개의 재고가 존재합니다");
            }
        }
        return new ResponseEntity<>(errorResponse, iE.getStatus());
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ErrorResponse> orderExHandler(OrderException oE) {
        log.info("orderExHandler");
        ErrorResponse errorResponse = ErrorResponse.buildErrorResponse(oE);
        return new ResponseEntity<>(errorResponse, oE.getStatus());
    }

    @ExceptionHandler(BasketException.class)
    public ResponseEntity<ErrorResponse> basketExHandler(BasketException bE) {
        log.info("orderExHandler");
        ErrorResponse errorResponse = ErrorResponse.buildErrorResponse(bE);
        return new ResponseEntity<>(errorResponse, bE.getStatus());
    }
}
