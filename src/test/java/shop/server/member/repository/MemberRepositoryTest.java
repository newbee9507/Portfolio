package shop.server.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import shop.server.basket.entity.Basket;
import shop.server.config.TestConfig;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.exception.error.member.MemberException;
import shop.server.member.dtos.MemberResponseDto;
import shop.server.member.entity.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @BeforeEach
    void settingData() {
        Member admin = Member.builder().id("admin").password("admin123").nickName("admin").point(0)
                .roles(List.of("ADMIN", "MEMBER")).basket(new Basket()).orderList(new ArrayList<>()).build();
        Member member = Member.builder().id("member").password("member123").nickName("member").point(0)
                .roles(List.of("MEMBER")).basket(new Basket()).orderList(new ArrayList<>()).build();
        em.persist(admin);
        em.persist(member);
    }
    @Test
    @DisplayName("조회실패 - 존재하지 않는 유저")
    void findMemberToResponseDtoFailTest() {
        Optional<MemberResponseDto> result = repository.findMemberToResponseDto(0L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("조회성공")
    void findMemberToResponseDtoTest() {
        Member findMember = em.find(Member.class, 1L);
        MemberResponseDto expected = new MemberResponseDto
                (findMember.getMemberId(), findMember.getId(), findMember.getNickName(), findMember.getPoint());

        MemberResponseDto result = repository.findMemberToResponseDto(findMember.getMemberId()).get();

        assertThat(expected).isEqualTo(result);
    }

    @Test
    @DisplayName("Id중복검사")
    void idDuplicationTest() {

    }

}