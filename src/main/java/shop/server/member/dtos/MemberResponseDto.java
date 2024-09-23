package shop.server.member.dtos;

import lombok.*;
import shop.server.order.entity.Order;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MemberResponseDto {

    private Long memberId;

    private String id;

    private String nickName;

    private Integer point;

}
