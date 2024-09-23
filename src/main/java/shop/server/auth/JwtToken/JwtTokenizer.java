package shop.server.auth.JwtToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@ConfigurationProperties("jwt.token")
public class JwtTokenizer {

    @Getter
    @NotEmpty
    private final String secretKey;

    @Getter
    @Range(min = 1)
    private final int accessTokenLifeTime;

    @Getter
    @Range(min = 1)
    private final int refreshTokenLifeTime;

    private final String bea = "Bearer ";

    public String encodeSecretKeyByBase64(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Map<String, Object> claims,
                              String subject,
                              Date expirationTime,
                              String encodedSecretKey) {
        Key key = createKeyByencodedSecretKey(encodedSecretKey);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expirationTime)
                .signWith(key)
                .compact();
    }

    public Jws<Claims> getClaims(String jws, String encodedSecretKey) {
        Key key = createKeyByencodedSecretKey(encodedSecretKey);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
    }

    public Date setTokenLifeTime(int lifeTime) {
        return Date.from(
                ZonedDateTime.now(
                        ZoneId.of("Asia/Seoul")).toInstant()
                        .plus(lifeTime,ChronoUnit.MINUTES));
    }

    private Key createKeyByencodedSecretKey(String encodedSecretKey) {
        byte[] decode = Decoders.BASE64.decode(encodedSecretKey);
        return Keys.hmacShaKeyFor(decode);
    }


    public JwtTokenizer(String secretKey, int accessTokenLifeTime, int refreshTokenLifeTime) {
        this.secretKey = secretKey;
        this.accessTokenLifeTime = accessTokenLifeTime;
        this.refreshTokenLifeTime = refreshTokenLifeTime;
    }
}
