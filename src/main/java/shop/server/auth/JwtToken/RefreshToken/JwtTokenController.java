package shop.server.auth.JwtToken.RefreshToken;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.exception.error.member.MemberException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/jwt")
@RequiredArgsConstructor
public final class JwtTokenController {

    private final JwtTokenService service;

    private final String refresh = "Refresh";

    @GetMapping("/newToken")
    public ResponseEntity<Map<String, String>> setNewTokenAndRedirect(HttpServletRequest request) {
        log.info("setNewTokenAndRedirect");
        String oldRefreshToken = request.getHeader(refresh);

        if (!service.isValid(oldRefreshToken)) {
            log.info("setNewTokenAndRedirect error");
            throw new MemberException(HttpStatus.UNAUTHORIZED, MemberExMessage.LOGIN_FIRST);
        }

        ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();
        tokens.put("acToken",service.createNewAccessToken(oldRefreshToken));
        tokens.put("rhToken",service.createNewRefreshToken(oldRefreshToken));

        service.setBlackList(oldRefreshToken);

        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }

    public RefreshToken saveToken(RefreshToken token) {
        return service.saveToken(token);
    }

}
