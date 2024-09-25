package shop.server.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import shop.server.auth.authorization.MemberAuthorityUtils;
import shop.server.basket.entity.Basket;
import shop.server.basket.repository.BasketRepository;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.exception.error.member.MemberException;
import shop.server.member.dtos.MemberResponseDto;
import shop.server.member.dtos.MemberSaveDto;
import shop.server.member.dtos.MemberUpdateDto;
import shop.server.member.entity.Member;
import shop.server.member.mapper.MemberMapper;
import shop.server.member.repository.MemberRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService service;

    @Mock
    private MemberRepository repository;

    @Mock
    private MemberMapper mapper;

    @Mock
    private MemberAuthorityUtils memberAuthorityUtils = new MemberAuthorityUtils();

    @Mock
    private PasswordEncoder passwordEncoder;

    private Member admin;

    @BeforeEach
    void memberSetting() {
        admin = Member.builder().memberId(1L).id("admin").password("123456789")
                .nickName("admin").point(0).roles(List.of("ADMIN","MEMBER"))
                .build();
    }

    @Test
    @DisplayName("가입성공")
    void save() {
        MemberSaveDto saveDto = new MemberSaveDto("admin", "123456789", "admin");
        MemberResponseDto expected
                = new MemberResponseDto(null, admin.getId(), admin.getNickName(), admin.getPoint());

        given(mapper.memberSaveDtotoMember(any(MemberSaveDto.class), any(List.class))).willReturn(admin);
        given(mapper.membertoMemberResponseDto(admin)).willReturn(expected);
        given(repository.checkExistIdOrNickName(saveDto)).willReturn(new HashMap<>());
        given(repository.save(any(Member.class))).willReturn(admin);

        MemberResponseDto result = service.save(saveDto);

        assertThat(expected.getId()).isEqualTo(result.getId());
        assertThat(expected.getNickName()).isEqualTo(result.getNickName());
    }

    @Test
    @DisplayName("가입실패 - 아이디 및 닉네임 중복")
    void saveFail() {
        MemberSaveDto idDuplication = new MemberSaveDto("error", "123456789", "admin");
        MemberSaveDto nickDuplication = new MemberSaveDto("admin", "123456789", "error");
        Map<Boolean, String> idErrorMap = Map.of(Boolean.TRUE, "idError");
        Map<Boolean, String> nickErrorMap = Map.of(Boolean.TRUE, "nickError");

        given(repository.checkExistIdOrNickName(idDuplication)).willReturn(idErrorMap);
        given(repository.checkExistIdOrNickName(nickDuplication)).willReturn(nickErrorMap);

        assertThatThrownBy(() -> service.save(idDuplication))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberExMessage.ID_DUPLICATION.getMessage());

        assertThatThrownBy(() -> service.save(nickDuplication))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberExMessage.NICKNAME_DUPLICATION.getMessage());
    }

    @Test
    @DisplayName("조회성공")
    void information() {
        MemberResponseDto expected
                = new MemberResponseDto(null, admin.getId(), admin.getNickName(), admin.getPoint());
        given(repository.findMemberToResponseDto(anyLong())).willReturn(Optional.of((expected)));

        MemberResponseDto result = service.information(1L);

        assertThat(expected).isEqualTo(result);
    }

    @Test
    @DisplayName("업데이트 성공")
    void update() {
        MemberUpdateDto updateDto = new MemberUpdateDto("newPw123456", "newName");
        MemberResponseDto expected
                = new MemberResponseDto(null, admin.getId(), updateDto.getNickName(), admin.getPoint());

        given(repository.existsByNickName(updateDto.getNickName())).willReturn(false);
        given(repository.findById(1L)).willReturn(Optional.of(admin));
        given(mapper.injectionValueIfNull(admin, updateDto, passwordEncoder)).willReturn(updateDto);
        given(mapper.membertoMemberResponseDto(admin)).willReturn(expected);

        MemberResponseDto result = service.update(1L, updateDto, admin.getId());

        assertThat(expected).isEqualTo(result);
    }
    @Test
    @DisplayName("업데이트 실패 - 닉네임 중복")
    void updateFail() {
        MemberUpdateDto updateDto = new MemberUpdateDto("newPw123456", "isNotValid");

        given(repository.existsByNickName(updateDto.getNickName())).willReturn(true);

        assertThatThrownBy(() -> service.update(1L, updateDto, admin.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberExMessage.NICKNAME_DUPLICATION.getMessage());
    }

    @Test
    @DisplayName("삭제성공")
    void deleteMember() {
        MemberResponseDto expected
                = new MemberResponseDto(1L, admin.getId(), admin.getNickName(), admin.getPoint());
        given(repository.delete(1L)).willReturn(expected);

        MemberResponseDto result = service.deleteMember(1L);

        assertThat(expected).isEqualTo(result);
    }


    @Test
    @DisplayName("기본 키로 조회성공")
    void findByMemberId() {
        given(repository.findById(1L)).willReturn(Optional.of(admin));

        Member result = service.findByMemberId(1L);

        assertThat(admin).isEqualTo(result);
    }

    @Test
    @DisplayName("기본 키로 조회실패")
    void findByMemberIdFail() {


        given(repository.findMemberToResponseDto(1L)).willThrow(new MemberException(HttpStatus.NOT_FOUND, MemberExMessage.NOT_EXIST));

        assertThatThrownBy(() -> service.information(1L))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberExMessage.NOT_EXIST.getMessage());
    }
}