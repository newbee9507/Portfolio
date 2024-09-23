package shop.server.auth.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import shop.server.auth.JwtToken.RefreshToken.JwtTokenController;
import shop.server.auth.authorization.MemberAuthorityUtils;
import shop.server.auth.memberdetails.MemberDetails;
import shop.server.auth.memberdetails.MemberDetailsService;
import shop.server.auth.JwtToken.JwtTokenizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtTokenizer tokenizer;
    private final MemberAuthorityUtils memberAuthorityUtils;
    private final MemberDetailsService memberDetailsService;

    private final String bea = "Bearer ";
    private final String authorization = "Authorization";
    private final String homeUrl = "/myShop/home";
    private final String jwtUrl = "/jwt/newToken";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            Map<String, Object> claims = verifyJws(request);
            log.info("claims = {}", claims.toString());
            setAuthenticationToContext(claims);
        } catch (SignatureException se) {
            request.setAttribute("ex", se);
            log.info("SignatureException");
        } catch (ExpiredJwtException ee) {
            String refreshToken = request.getHeader("Refresh");
            log.info("ExpiredJwtException, token = {}",refreshToken);
            if (refreshToken == null || refreshToken.isEmpty()) {
                log.info("refreshToken is null");
                response.sendRedirect(homeUrl);
                return;
            }
            response.sendRedirect(jwtUrl);
            return;
        } catch (AccessDeniedException iE) {
            response.sendRedirect("/myShop/home");
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String token = request.getHeader(authorization);
        String requestURI = request.getRequestURI();
        return token == null || !token.startsWith(bea) || requestURI.equals(homeUrl) || requestURI.equals(jwtUrl);
    }

    private Map<String, Object> verifyJws(HttpServletRequest request) {

        String jws = request.getHeader(authorization).replace(bea, "");
        String encodedSecretKey = tokenizer.encodeSecretKeyByBase64(tokenizer.getSecretKey());

        return tokenizer.getClaims(jws, encodedSecretKey).getBody();
    }

    private void setAuthenticationToContext(Map<String, Object> claims) {
        String id = (String) claims.get("id");
        MemberDetails member = (MemberDetails) memberDetailsService.loadUserByUsername(id);
        List<GrantedAuthority> authorities = memberAuthorityUtils.createAuthorities(member.getRoles());

        Authentication authentication
                = new UsernamePasswordAuthenticationToken(member, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
}
