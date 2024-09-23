package shop.server.member.dtos;

import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import shop.server.item.entity.Item;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MemberSaveDto {

    @NotBlank
    @Length(min = 4, max = 12)
    private String id;

    @NotBlank
    @Length(min = 8, max = 16)
    private String password;

    @NotBlank
    @Length(min = 2, max = 6)
    private String nickName;
}
