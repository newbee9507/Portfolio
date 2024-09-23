package shop.server.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shop.server.member.entity.Member;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long>, DslMemberRepository {

    boolean existsByNickName(String nickName);

    Optional<Member> findById(String id);

}
