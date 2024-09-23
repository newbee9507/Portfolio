package shop.server.exception.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

    private static final String zondId = "Asia/Seoul";

    private final int status;

    private final String message;

    private final ZonedDateTime occurrenceTime;

    private List<fieldError> fieldErrors;

    private record fieldError(String field, String message) {}

    public void setFieldErrors(String field, String message) {
        if(Objects.isNull(fieldErrors)) fieldErrors = new ArrayList<>();
        fieldErrors.add(new fieldError(field, message));
    }

    public static ErrorResponse buildErrorResponse(MyShopException ex) {
        return new ErrorResponse(ex.getStatus().value(), ex.getMessage(), ZonedDateTime.now(ZoneId.of(zondId)));
    }
}
