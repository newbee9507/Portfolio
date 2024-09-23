package shop.server.exception.error.member;

import lombok.Getter;

@Getter
public enum MemberExMessage implements shop.server.exception.error.ErrorMessage {
    ID_DUPLICATION("이미 가입된 아이디입니다"),
    ID_NOT_VALID("아이디는 4글자 이상, 12글자 이하여야 합니다."),
    NICKNAME_DUPLICATION("이미 가입된 닉네임입니다"),
    NICKNAME_NOT_VALID("닉네임은 2글자 이상, 6글자 이하여야 합니다."),
    CHECK_ID_PW("아이디와 비밀번호를 정확히 입력해주세요."),
    CHECK_YOUR_DATA("입력한 정보들 중 하나 이상이 잘못되었습니다. 아래의 정보를 확인해주세요."),
    UNAUTHORIZED("본인의 정보만 접근 가능합니다"),
    FORBIDDEN("관리자만 접근 가능한 기능입니다."),
    NOT_EXIST("존재하지 않는 회원입니다"),
    LOGIN_FAIL("아이디 혹은 비밀번호를 다시 확인하시기 바랍니다"),
    LOGIN_FIRST("로그인 후 재시도 바랍니다."),
    BASKET_ERROR("회원님의 장바구니에 오류가 발생했습니다. 관리자에게 문의해주세요"),
    NOT_ENOUGH_POINT("포인트가 부족합니다. 충전 후 다시 시도해주세요."),
    SERVER_ERROR("일시적 서버 오류입니다. 잠시 후 다시 시도해주세요");

    private final String message;

    MemberExMessage(String message) {
        this.message = message;
    }
}
