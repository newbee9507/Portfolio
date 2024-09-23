package shop.server.auth.memberdetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import shop.server.auth.dto.MemberDetailDto;
import shop.server.member.entity.Member;

import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDetails implements UserDetails {

    private Long memberId;
    private String id;
    private String password;
    private List<String> roles;

    public MemberDetails(MemberDetailDto member) {
        this.memberId = member.getMemberId();
        this.id = member.getId();
        this.password = member.getPassword();
        this.roles = member.getRoles();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return id;
    }
}
