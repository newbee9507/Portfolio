package shop.server.member.dtos;

import jakarta.annotation.Nullable;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MemberUpdateDto {

    @Length(min = 8, max = 16)
    private String password;

    @Length(min = 2, max = 6)
    private String nickName;
}
