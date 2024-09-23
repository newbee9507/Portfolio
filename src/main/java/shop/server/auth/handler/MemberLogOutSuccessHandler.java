package shop.server.auth.handler;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.StringUtils;
import shop.server.auth.JwtToken.JwtTokenizer;
import shop.server.auth.JwtToken.RefreshToken.RefreshToken;
import shop.server.auth.JwtToken.RefreshToken.RefreshTokenRepository;
import shop.server.member.entity.Member;

import java.io.IOException;
import java.lang.reflect.Field;

@RequiredArgsConstructor
public class MemberLogOutSuccessHandler implements LogoutHandler, LogoutSuccessHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    private final String bea = "Bearer ";

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        
        String rhToken = request.getHeader("Refresh");
        RefreshToken refreshToken = refreshTokenRepository.findByToken(rhToken.replaceAll(bea, ""))
                .orElseThrow(() -> new AccessDeniedException("AccessDeniedException"));
        refreshTokenRepository.delete(refreshToken);

    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        response.sendRedirect("/myShop/home");
    }
}
