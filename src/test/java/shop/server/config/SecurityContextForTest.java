package shop.server.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import shop.server.auth.dto.MemberDetailDto;
import shop.server.auth.memberdetails.MemberDetails;
import shop.server.member.entity.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityContextForTest implements WithSecurityContextFactory<UserDetailsForTest> {

    @Override
    public SecurityContext createSecurityContext(UserDetailsForTest annotation) {
        final SecurityContext context = SecurityContextHolder.createEmptyContext();

        Member member = Member.builder()
                .memberId(annotation.memberId())
                .id(annotation.id())
                .nickName(annotation.nickName())
                .point(annotation.point())
                .roles(List.of(annotation.roles()))
                .build();

        MemberDetails memberDetails =
                new MemberDetails(new MemberDetailDto(member.getMemberId(), member.getId(), member.getPassword(), member.getRoles()));
        List<String> roles = Arrays.asList(annotation.roles());

        List<GrantedAuthority> authorities =
                roles.stream().map(str -> new SimpleGrantedAuthority("ROLE_"+ str)).collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(
                        memberDetails, null, authorities);

        context.setAuthentication(authenticationToken);
        return context;
    }
}
