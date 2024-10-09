package shop.server.member.repository;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import shop.server.auth.dto.MemberDetailDto;
import shop.server.member.dtos.MemberResponseDto;
import shop.server.member.dtos.MemberSaveDto;
import shop.server.member.entity.Member;

import java.util.Map;
import java.util.Optional;

@Component
public interface DslMemberRepository {

    Map<Boolean, String> checkExistIdOrNickName(MemberSaveDto saveDto);

    MemberDetailDto findMemberToDetailDto(String id);

    Optional<MemberResponseDto> findMemberToResponseDto(Long memberId);

    MemberResponseDto delete(Long memberId);

    Member findByIdFetchOrderList(Long memberId);

}
