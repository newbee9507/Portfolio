package shop.server.exception.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.server.exception.error.ErrorResponse;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.exception.error.member.MemberException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class AnotherExHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> MemberArgumentValidHandler(MethodArgumentNotValidException me) {
        ErrorResponse errorResponse =
                ErrorResponse.buildErrorResponse(
                        new MemberException(HttpStatus.BAD_REQUEST, MemberExMessage.CHECK_YOUR_DATA));

        List<FieldError> fieldErrors = me.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            errorResponse.setFieldErrors(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
