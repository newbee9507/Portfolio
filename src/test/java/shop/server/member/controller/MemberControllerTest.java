package shop.server.member.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import shop.server.config.TestConfig;
import shop.server.config.UserDetailsForTest;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.exception.error.member.MemberException;
import shop.server.member.dtos.MemberResponseDto;
import shop.server.member.dtos.MemberSaveDto;
import shop.server.member.dtos.MemberUpdateDto;
import shop.server.member.entity.Member;
import shop.server.member.service.MemberService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = MemberController.class)
@ExtendWith({MockitoExtension.class})
@Import(TestConfig.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService service;

    @Autowired
    private Gson gson;

    @Autowired
    private PasswordEncoder encoder;

    private Member admin;
    private final String baseUrl = "/myShop/member/";

    @BeforeEach
    public void memberSetting() {
        admin = Member.builder().memberId(1L).id("admin").password(encoder.encode("123456789"))
                .nickName("admin").point(0).roles(List.of("ADMIN"))
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUpPass() throws Exception {
        MemberSaveDto saveDto = new MemberSaveDto("admin", "123456789", "admin");
        MemberResponseDto result = new MemberResponseDto(
                1L, saveDto.getId(), saveDto.getNickName(), 0);
        given(service.save(any(MemberSaveDto.class))).willReturn(result);

        ResultActions saveAction = mockMvc.perform(post(baseUrl+"signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(saveDto)));

        saveAction.andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(result.getMemberId()))
                .andExpect(jsonPath("$.id").value(result.getId()))
                .andExpect(jsonPath("$.nickName").value(result.getNickName()))
                .andExpect(jsonPath("$.point").value(result.getPoint()));
    }
    @Test
    @DisplayName("회원가입 실패 - 아이디 또는 닉네임 중복")
    void signUpErrorById() throws Exception {
        MemberSaveDto idError = new MemberSaveDto("admin", "123456789", "admin");
        MemberSaveDto nickNameError = new MemberSaveDto("nick", "123456789", "nick");
        given(service.save(idError))
                .willThrow(new MemberException(HttpStatus.BAD_REQUEST, MemberExMessage.ID_DUPLICATION));

        given(service.save(nickNameError))
                .willThrow(new MemberException(HttpStatus.BAD_REQUEST, MemberExMessage.NICKNAME_DUPLICATION));

        ResultActions idDuplication = mockMvc.perform(post(baseUrl+"signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(idError)));

        ResultActions nickNameDuplication = mockMvc.perform(post(baseUrl+"signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(nickNameError)));

        idDuplication.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(MemberExMessage.ID_DUPLICATION.getMessage()));

        nickNameDuplication.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(MemberExMessage.NICKNAME_DUPLICATION.getMessage()));
    }

    @Test
    @DisplayName("회원가입 실패 - 아이디 또는 닉네임 검증실패")
    void signUpErrorNotValid() throws Exception {
        MemberSaveDto idError = new MemberSaveDto("a", "123456789", "admin");
        MemberSaveDto nickNameError = new MemberSaveDto("nick", "123456789", "n");

        given(service.save(idError))
                .willThrow(new MemberException(HttpStatus.BAD_REQUEST, MemberExMessage.ID_NOT_VALID));
        given(service.save(nickNameError))
                .willThrow(new MemberException(HttpStatus.BAD_REQUEST, MemberExMessage.ID_NOT_VALID));

        ResultActions idValidError = mockMvc.perform(post(baseUrl+"signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(idError)));
        ResultActions nickNameValidError = mockMvc.perform(post(baseUrl+"signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(nickNameError)));

        idValidError.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.fieldErrors").isArray());

        nickNameValidError.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("조회성공")
    @UserDetailsForTest()
    void getInfo() throws Exception {
        MemberResponseDto result
                = new MemberResponseDto(1L, "admin", "admin", 0);

        given(service.information(1L)).willReturn(result);
//        given(service.findByMemberId(1L)).willReturn(admin);

        ResultActions requestGetInfo = mockMvc.perform(get(baseUrl+"info/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        requestGetInfo.andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(result.getMemberId()))
                .andExpect(jsonPath("$.id").value(result.getId()))
                .andExpect(jsonPath("$.nickName").value(result.getNickName()))
                .andExpect(jsonPath("$.point").value(result.getPoint()));
    }

    @Test
    @DisplayName("조회실패 - 본인정보만 조회가능")
    @UserDetailsForTest(memberId = 3L, id = "member2", nickName = "member2", roles = "MEMBER")
    void getInfoFail() throws Exception {
        MemberResponseDto result
                = new MemberResponseDto(2L, "member", "member", 0);

        given(service.information(2L)).willReturn(result);

        ResultActions requestGetInfo = mockMvc.perform(get(baseUrl+"info/2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        requestGetInfo.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.message").value(MemberExMessage.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("정보수정 성공")
    @UserDetailsForTest(roles = {"ADMIN","MEMBER"})
    void updateInfo() throws Exception {
        MemberUpdateDto updateDto = new MemberUpdateDto("123456789", "member");
        MemberResponseDto result
                = new MemberResponseDto(1L, admin.getId(), updateDto.getNickName(), 0);

        given(service.update(1L, updateDto, admin.getId())).willReturn(result);

        ResultActions updateRequest = mockMvc.perform(patch(baseUrl+"update/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(updateDto)));

        updateRequest.andExpect(status().isOk())
                .andExpect(jsonPath("$.nickName").value(updateDto.getNickName()));
    }
    @Test
    @DisplayName("정보수정 실패 - 입력값 검증실패")
    @UserDetailsForTest(roles = {"ADMIN","MEMBER"})
    void updateInfoFail() throws Exception {
        MemberUpdateDto pwNotValid = new MemberUpdateDto("1239", "member");
        MemberUpdateDto nameNotValid = new MemberUpdateDto("123456789", "isMustError");

        ResultActions pwRequest = mockMvc.perform(patch(baseUrl+"update/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(pwNotValid)));
        ResultActions nameRequest = mockMvc.perform(patch(baseUrl+"update/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(nameNotValid)));

        pwRequest.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.fieldErrors").isArray());
        nameRequest.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("포인트 입금 성공")
    @UserDetailsForTest(roles = {"ADMIN","MEMBER"})
    void addPoint() throws Exception {
        Integer point = 10000;
        MemberResponseDto expected =
                new MemberResponseDto(admin.getMemberId(), admin.getId(), admin.getNickName(), point);
        given(service.addPoint(1L, point)).willReturn(expected);

        MemberResponseDto result = service.addPoint(1L, point);

        ResultActions addRequest = mockMvc.perform(patch(baseUrl + "addPoint/1/10000")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        addRequest.andExpect(status().isOk());
        assertThat(expected).isEqualTo(result);
    }

    @Test
    @UserDetailsForTest(roles = {"ADMIN","MEMBER"})
    void deleteMember() throws Exception {
        MemberResponseDto expected
                = new MemberResponseDto(1L, "admin", "admin", 0);
        given(service.deleteMember(1L)).willReturn(expected);

        MemberResponseDto result = service.deleteMember(1L);
        ResultActions deleteRequest = mockMvc.perform(delete(baseUrl + "delete/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        deleteRequest.andExpect(status().isOk());
        assertThat(expected).isEqualTo(result);
    }
}