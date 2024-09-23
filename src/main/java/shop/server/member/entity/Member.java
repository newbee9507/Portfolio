package shop.server.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import shop.server.basket.entity.Basket;
import shop.server.member.dtos.MemberUpdateDto;
import shop.server.order.entity.Order;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member {

    @Id
    @Column(name = "member_Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @NotEmpty
    @Column(name = "id", unique = true)
    private String id;

    @NotEmpty
    @Column(name = "passWord")
    private String password;

    @NotEmpty
    @Column(name = "nickName", unique = true)
    private String nickName;

    @Column(name = "point")
    private int point;

    @NotEmpty
    @Builder.Default
    private List<String> roles= new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    private Basket basket;

    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    @OneToMany(mappedBy = "client",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orderList = new ArrayList<>();

    public void updateInfo(MemberUpdateDto updateDto) {
        this.password = updateDto.getPassword();
        this.nickName = updateDto.getNickName();
    }

    public void addPoint(Integer point) {
        this.point += point;
    }

    public void mappingBasket(Basket basket) {
        this.basket = basket;
    }
}

