package shop.server.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import shop.server.exception.error.ErrorResponse;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.exception.error.member.MemberException;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class MemberAuthenticationFailHandler implements AuthenticationEntryPoint{
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.error("MemberAuthenticationFail");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = ErrorResponse.buildErrorResponse(
                new MemberException(HttpStatus.BAD_REQUEST, MemberExMessage.LOGIN_FIRST));

        if (authException instanceof InsufficientAuthenticationException) {
            errorResponse = ErrorResponse.buildErrorResponse(
                    new MemberException(HttpStatus.FORBIDDEN, MemberExMessage.FORBIDDEN));
        }

        response.setStatus(errorResponse.getStatus());
        response.setHeader("ex",authException.getMessage());
        response.setHeader("ex1",authException.getClass().getName());

        PrintWriter writer = response.getWriter();
        objectMapper.writeValue(writer, errorResponse);
    }
}
