package shop.server.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shop.server.auth.JwtToken.JwtTokenizer;
import shop.server.auth.JwtToken.RefreshToken.JwtTokenController;
import shop.server.auth.JwtToken.RefreshToken.RefreshToken;
import shop.server.auth.dto.LoginDto;
import shop.server.auth.memberdetails.MemberDetails;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer tokenizer;
    private final JwtTokenController jwtTokenController;

    private final String bea = "Bearer ";
    private final String authorization = "Authorization";
    private final String expirationTime = "ExpirationTime";
    private final String refresh = "Refresh";

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

        UsernamePasswordAuthenticationToken req =
                new UsernamePasswordAuthenticationToken(loginDto.getId(), loginDto.getPassword());

        return authenticationManager.authenticate(req);
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        MemberDetails member = (MemberDetails) authResult.getPrincipal();
        String accessToken = delegateCreateAccessToken(member);
        String refreshToken = delegateCreateRefreshToken(member);

        Date accessTokenExpirationTime =
                tokenizer.setTokenLifeTime(tokenizer.getAccessTokenLifeTime());
        Date refreshTokenExpirationTime =
                tokenizer.setTokenLifeTime(tokenizer.getRefreshTokenLifeTime());

        response.setHeader(authorization, bea + accessToken);
        response.setHeader(refresh, bea + refreshToken);
        response.setHeader(expirationTime, String.valueOf(accessTokenExpirationTime));

        jwtTokenController.saveToken(new RefreshToken(refreshToken, refreshTokenExpirationTime, false));
        getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

    private String delegateCreateAccessToken(MemberDetails member) {
        Map<String, Object> claims = new ConcurrentHashMap<>();

        claims.put("id", member.getId());
        claims.put("roles", member.getRoles());
        String subject = member.getUsername();
        Date expirationTime = tokenizer.setTokenLifeTime(tokenizer.getAccessTokenLifeTime());
        String encodedSecretKey = tokenizer.encodeSecretKeyByBase64(tokenizer.getSecretKey());

        return tokenizer.createToken(claims, subject, expirationTime, encodedSecretKey);
    }

    private String delegateCreateRefreshToken(MemberDetails member) {
        Map<String, Object> claims = new ConcurrentHashMap<>();

        claims.put("id", member.getId());
        String subject = member.getId();
        Date expirationTime = tokenizer.setTokenLifeTime(tokenizer.getRefreshTokenLifeTime());
        String encodedSecretKey = tokenizer.encodeSecretKeyByBase64(tokenizer.getSecretKey());

        return tokenizer.createToken(claims, subject, expirationTime, encodedSecretKey);
    }
}
