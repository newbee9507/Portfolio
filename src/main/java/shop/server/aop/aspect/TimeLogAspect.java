package shop.server.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Aspect
public class TimeLogAspect {

    @Around("@annotation(shop.server.aop.annotation.TimeLog)")
    public Object TimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        String location = ZoneId.systemDefault().getId();
        String startTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ")) + location;
        log.info("요청시간 = {}", startTime);

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        log.info("실행시간 = {}ms", end - start);

        return result;
    }
}
