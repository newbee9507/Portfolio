package shop.server.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import shop.server.exception.error.ErrorResponse;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.exception.error.member.MemberException;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class MemberLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        log.error("MemberLoginFail");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());


        ErrorResponse errorResponse = ErrorResponse.buildErrorResponse(
                new MemberException(HttpStatus.BAD_REQUEST, MemberExMessage.LOGIN_FAIL));

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(errorResponse.getStatus());
        response.setHeader("ex", exception.getMessage());

        PrintWriter writer = response.getWriter();
        objectMapper.writeValue(writer, errorResponse);
        writer.flush();

    }
}
