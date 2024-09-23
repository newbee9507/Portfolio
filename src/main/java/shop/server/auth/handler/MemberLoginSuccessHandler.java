package shop.server.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import shop.server.auth.dto.LoginSuccessResponseDto;
import shop.server.auth.memberdetails.MemberDetails;

import java.io.IOException;
import java.util.List;

public class MemberLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final String isMember = "MEMBER";
    private final String isAdmin = "ADMIN";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        ObjectMapper objectMapper = new ObjectMapper();
        MemberDetails principal = (MemberDetails) authentication.getPrincipal();

        Long memberId = principal.getMemberId();
        String id = principal.getUsername();
        String role = isMember;

        List<String> roles = principal.getRoles();
        if (roles.contains(isAdmin)) {
            role = isAdmin;
        }

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), new LoginSuccessResponseDto(memberId,id,role));

    }
}
