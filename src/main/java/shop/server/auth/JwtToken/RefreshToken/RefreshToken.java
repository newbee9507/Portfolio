package shop.server.auth.JwtToken.RefreshToken;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@RedisHash(value = "refreshToken" ,timeToLive = 3600L)
public class RefreshToken {

    @Id
    private String token;

    @EqualsAndHashCode.Exclude
    private Date expiration;

    @EqualsAndHashCode.Exclude
    @Setter
    private boolean isBlackListed;

}
