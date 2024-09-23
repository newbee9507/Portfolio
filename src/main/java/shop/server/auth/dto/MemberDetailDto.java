package shop.server.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailDto {

    private Long memberId;
    private String id;
    private String password;
    private List<String> roles;
}
