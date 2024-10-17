package shop.server.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import shop.server.auth.memberdetails.MemberDetails;
import shop.server.exception.error.MyShopException;
import shop.server.exception.error.member.MemberException;
import shop.server.member.dtos.MemberResponseDto;
import shop.server.member.dtos.MemberSaveDto;
import shop.server.member.entity.Member;

@Slf4j
@Aspect
public class MemberAspect {

    @Pointcut("execution(* shop.server..*(..))")
    public void appPointcut(){}

    @Pointcut("execution(* *(shop.server.auth.memberdetails.MemberDetails, ..))")
    public void MemberDetailsPointcut(){}

    @Before("appPointcut() && MemberDetailsPointcut()")
    public void requestStart(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().getName();
        MemberDetails member = (MemberDetails) joinPoint.getArgs()[0];

        log.info("[{}]번 회원이 [{}] 요청", member.getMemberId(), method);
    }

    @AfterReturning("MappPointcut() && MemberDetailsPointcut()")
    public void requestEnd(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().getName();
        MemberDetails member = (MemberDetails) joinPoint.getArgs()[0];

        log.info("[{}번] 회원의 [{}] 요청 정상반환", member.getMemberId(), method);
    }

    @AfterThrowing(value = "appPointcut() && MemberDetailsPointcut()", throwing = "ex")
    public void requestError(JoinPoint joinPoint, MyShopException ex) {
        String method = joinPoint.getSignature().getName();
        MemberDetails member = (MemberDetails) joinPoint.getArgs()[0];
        log.info("{}번 회원 {} 요청 에러. -> {}",member.getMemberId(), method, ex.getMessage());
    }
}
