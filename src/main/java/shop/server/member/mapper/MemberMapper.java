package shop.server.member.mapper;

import org.mapstruct.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import shop.server.member.dtos.MemberResponseDto;
import shop.server.member.dtos.MemberSaveDto;
import shop.server.member.dtos.MemberUpdateDto;
import shop.server.member.entity.Member;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {

    @Mapping(source = "roles", target = "roles")
    Member memberSaveDtotoMember(MemberSaveDto saveDto, List<String> roles);

    MemberResponseDto membertoMemberResponseDto(Member member);

    default MemberUpdateDto injectionValueIfNull(Member member, MemberUpdateDto dto, PasswordEncoder encoder) {
        String password = StringUtils.hasText(dto.getPassword()) ?
                encoder.encode(dto.getPassword()) : member.getPassword();

        String nickName = StringUtils.hasText(dto.getNickName()) ?
                dto.getNickName() : member.getNickName();

        return new MemberUpdateDto(password, nickName);
    }

}
