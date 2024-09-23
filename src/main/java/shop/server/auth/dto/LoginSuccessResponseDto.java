package shop.server.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginSuccessResponseDto {

    private Long memberId;

    private String id;

    private String roles;
}
