package shop.server.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import shop.server.exception.error.ErrorResponse;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.exception.error.member.MemberException;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class MemberAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.error("MemberAccessDeniedHandler");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = ErrorResponse.buildErrorResponse(
                new MemberException(HttpStatus.FORBIDDEN, MemberExMessage.FORBIDDEN));

        response.setStatus(errorResponse.getStatus());

        PrintWriter writer = response.getWriter();
        objectMapper.writeValue(writer, errorResponse);
    }
}
