package shop.server.auth.JwtToken.RefreshToken;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import shop.server.auth.JwtToken.JwtTokenizer;
import shop.server.auth.authorization.MemberAuthorityUtils;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.exception.error.member.MemberException;
import shop.server.member.entity.Member;
import shop.server.member.repository.MemberRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtTokenizer tokenizer;
    private final MemberRepository memberRepository;
    private final RedisKeyValueAdapter redisKeyValueAdapter;
    private final RefreshTokenRepository refreshTokenRepository;

    private final String bea = "Bearer ";

    public RefreshToken saveToken(RefreshToken token) {
        return refreshTokenRepository.save(token);
    }

    public String createNewAccessToken(String oldFhToken) {
        log.info("createNewAccessToken");
        String secretKey = tokenizer.encodeSecretKeyByBase64(tokenizer.getSecretKey());
        String jws = tokenToJws(oldFhToken);

        Claims claims = tokenizer.getClaims(jws, secretKey).getBody();
        String id = (String) claims.get("id");

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(HttpStatus.NOT_FOUND, MemberExMessage.NOT_EXIST));
        claims.put("roles", member.getRoles());

        Date expirationTime = tokenizer.setTokenLifeTime(tokenizer.getAccessTokenLifeTime());

        return bea + tokenizer.createToken(claims, id, expirationTime, secretKey);
    }

    public String createNewRefreshToken(String oldFhToken) {
        log.info("createNewRefreshToken");
        String secretKey = tokenizer.encodeSecretKeyByBase64(tokenizer.getSecretKey());
        String jws = tokenToJws(oldFhToken);

        Claims claims = tokenizer.getClaims(jws, secretKey).getBody();
        String subject = (String) claims.get("id");
        Date expirationTime = tokenizer.setTokenLifeTime(tokenizer.getRefreshTokenLifeTime());

        String newRefreshToken = tokenizer.createToken(claims, subject, expirationTime, secretKey);
        saveToken(new RefreshToken(newRefreshToken, expirationTime, false));

        return bea + newRefreshToken;
    }

    public boolean isValid(String refreshToken) {
        log.info("token is Valid?");

        String jws = tokenToJws(refreshToken);
        Optional<RefreshToken> search = refreshTokenRepository.findByToken(jws);

        return search.isPresent() && isNotExpiration(search.get()) && search.get().isBlackListed() == false;
    }

    public void setBlackList(String oldRefreshToken) {
        log.info("setBlackList");
        String jws = tokenToJws(oldRefreshToken);
        RefreshToken oldToken = refreshTokenRepository.findByToken(jws).get();
        PartialUpdate<RefreshToken> addBlackList =
                new PartialUpdate<>(oldToken.getToken(), RefreshToken.class).set("isBlackListed", true);
        redisKeyValueAdapter.update(addBlackList);
    }

    private boolean isNotExpiration(RefreshToken token) {
        log.info("isNotExpiration");
        Date now = Date.from(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant());
        return now.before(token.getExpiration());
    }

    private String tokenToJws(String JwtToken) {
        return JwtToken.replaceAll(bea, "");
    }
}
