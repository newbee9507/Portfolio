package shop.server.auth.memberdetails;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import shop.server.auth.dto.MemberDetailDto;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.exception.error.member.MemberException;
import shop.server.member.entity.Member;
import shop.server.member.repository.MemberRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Cacheable(value = "member", key = "#id")
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        MemberDetailDto member = memberRepository.findMemberToDetailDto(id);
        if (Objects.isNull(member)) {
            throw new MemberException(HttpStatus.NOT_FOUND, MemberExMessage.NOT_EXIST);
        }
        return new MemberDetails(member);
    }
}
