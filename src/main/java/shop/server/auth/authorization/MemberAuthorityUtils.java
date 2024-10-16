package shop.server.auth.authorization;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberAuthorityUtils {

    @Value("${authority.id.admin}")
    private String adminId;

    private final List<String> ADMIN_ROLES = List.of("ADMIN", "MEMBER");
    private final List<String> MEMBER_ROLES = List.of("MEMBER");

    public List<String> createRoles(String id) {
        if (id.equals(adminId)) {
            return ADMIN_ROLES;
        }
        return MEMBER_ROLES;
    }

    public List<GrantedAuthority> createAuthorities(List<String> roles) {
        return roles.stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str))
                             .collect(Collectors.toList());
    }
}
