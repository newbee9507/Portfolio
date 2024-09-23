package shop.server.order.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import shop.server.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "orders")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"client", "itemList", "address", "totalPrice", "orderTime","arrivalTime"})
public class Order {

    @Id
    @Column(name = "order_Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_Id")
    private Member client;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> itemList = new ArrayList<>();

    private String address;

    private Integer totalPrice;

    private String orderTime;

    @Setter
    private String arrivalTime;

    public void mappringMember(Member member) {
        this.client = member;
        member.getOrderList().add(this);
    }
}
