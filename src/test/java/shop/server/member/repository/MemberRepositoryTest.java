package shop.server.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import shop.server.auth.dto.MemberDetailDto;
import shop.server.basket.entity.Basket;
import shop.server.config.TestConfig;
import shop.server.member.dtos.MemberResponseDto;
import shop.server.member.dtos.MemberSaveDto;
import shop.server.member.entity.Member;
import shop.server.member.entity.QMember;
import shop.server.order.entity.Order;
import shop.server.order.entity.OrderItem;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static shop.server.member.entity.QMember.member;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(TestConfig.class)
class MemberRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    MemberRepository repository;

    private final String adminId = "admin";
    private final String memberId = "member";
    private final String password = "password1234";
    private final String pass = "pass";
    private final String fail = "fail";

    @BeforeEach
    void settingData() {
        Member admin = Member.builder().id(adminId).password(password).nickName(adminId).point(0)
                .roles(List.of("ADMIN", "MEMBER")).basket(new Basket()).orderList(new ArrayList<>()).build();
        Member member = Member.builder().id(memberId).password(password).nickName(memberId).point(0)
                .roles(List.of("MEMBER")).basket(new Basket()).orderList(new ArrayList<>()).build();

        Order order = Order.builder().build();
        OrderItem orderItem = OrderItem.builder().build();
        orderItem.mappingOrder(order);
        order.mappringMember(admin);

        em.persist(admin);
        em.persist(member);
    }
    @Test
    @DisplayName("중복검사 - 통과")
    void duplicationTest() {
        MemberSaveDto memberSaveDto = new MemberSaveDto(pass, password, pass);
        Map<Boolean, String> result = repository.checkExistIdOrNickName(memberSaveDto);

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("아이디중복검사 - 실패")
    void idDuplicationFailTest() {
        MemberSaveDto memberSaveDto = new MemberSaveDto(adminId, password, pass);
        Map<Boolean, String> result = repository.checkExistIdOrNickName(memberSaveDto);

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("닉네임중복검사 - 실패")
    void duplicationFailTest() {
        MemberSaveDto memberSaveDto = new MemberSaveDto(pass, password, memberId);
        Map<Boolean, String> result = repository.checkExistIdOrNickName(memberSaveDto);

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Details조회 성공")
    void findMemberToDetailDtoTest() {
        MemberDetailDto expected = new MemberDetailDto(1L, adminId, password, List.of("ADMIN", "MEMBER"));

        MemberDetailDto result = repository.findMemberToDetailDto(adminId);

        assertThat(expected.getId()).isEqualTo(result.getId());
        assertThat(expected.getPassword()).isEqualTo(result.getPassword());
        assertThat(expected.getRoles()).isEqualTo(result.getRoles());
    }

    @Test
    @DisplayName("Details조회 실패")
    void findMemberToDetailDtoFailTest() {
        MemberDetailDto result = repository.findMemberToDetailDto(fail);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("조회성공")
    void findMemberToResponseDtoTest() {
        Member findMember = queryFactory.select(member)
                .from(member).where(member.id.eq(adminId)).fetchOne();
        MemberResponseDto expected = new MemberResponseDto
                (findMember.getMemberId(), findMember.getId(), findMember.getNickName(), findMember.getPoint());

        MemberResponseDto result = repository.findMemberToResponseDto(findMember.getMemberId()).get();

        assertThat(expected).isEqualTo(result);
    }

    @Test
    @DisplayName("조회실패 - 존재하지 않는 유저")
    void findMemberToResponseDtoFailTest() {
        Optional<MemberResponseDto> result = repository.findMemberToResponseDto(0L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("삭제성공")
    void deleteTest() {
        Long memberId = queryFactory.select(member.memberId)
                .from(member).where(member.id.eq(adminId)).fetchOne();
        MemberResponseDto expected = new MemberResponseDto(memberId, adminId, adminId, 0);

        MemberResponseDto result = repository.delete(memberId);

        assertThat(expected).isEqualTo(result);
    }

    @Test
    @DisplayName("오더리스트 페치조인 성공")
    void findByIdFetchOrderList() {
        Member findMember = queryFactory.select(member)
                .from(member).where(member.id.eq(adminId)).fetchOne();

        Member result = repository.findByIdFetchOrderList(findMember.getMemberId());

        assertThat(result.getOrderList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("오더리스트 페치조인 null")
    void findByIdFetchOrderListIsNull() {
        Long memberId = queryFactory.select(member.memberId)
                .from(member).where(member.id.eq(this.memberId)).fetchOne();

        Member result = repository.findByIdFetchOrderList(memberId);

        assertThat(result).isNull();
    }
}