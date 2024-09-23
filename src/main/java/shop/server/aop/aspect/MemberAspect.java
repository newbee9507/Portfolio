package shop.server.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import shop.server.exception.error.member.MemberException;
import shop.server.member.dtos.MemberResponseDto;
import shop.server.member.dtos.MemberSaveDto;
import shop.server.member.entity.Member;

@Slf4j
@Aspect
public class MemberAspect {

    @Pointcut("execution(* shop.server.member..*(..))")
    public void memberPackage(){}

    @Before("memberPackage() && bean(*Service) && args(obj,..)")
    public void serviceStart(JoinPoint joinPoint, Object obj) {
        String method = joinPoint.getSignature().getName();
        Long memberId = null;
        if (obj instanceof MemberSaveDto saveDto) {
            log.info("[{}]로 {} 요청", saveDto.getId(), method);
        } else if (obj instanceof Member member) {
            memberId = member.getMemberId();
        } else
            memberId = (Long) obj;
            log.info("[{}]번 회원이 [{}] 요청", memberId, method);

    }

    @AfterReturning(value = "memberPackage() && bean(*Service)",
            returning = "result")
    public void serviceEnd(JoinPoint joinPoint, MemberResponseDto result) {
        String request = joinPoint.getSignature().getName();
        log.info("[{}번] 회원의 [{}] 요청 정상반환", result.getId(), request);
    }

    @AfterThrowing(value = "memberPackage() && bean(*Service) && args(memberId,..)", throwing = "ex")
    public void serviceError(JoinPoint joinPoint, MemberException ex, Long memberId) {
        String method = joinPoint.getSignature().getName();
        log.info("{}번 회원 {} 요청 에러. -> {}",memberId, method, ex.getMessage());
    }
}
