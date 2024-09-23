package shop.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.server.aop.aspect.MemberAspect;
import shop.server.aop.aspect.TimeLogAspect;

@Configuration
public class AopConfig {

    @Bean
    public TimeLogAspect timeLogAspect() {
        return new TimeLogAspect();
    }

    @Bean
    public MemberAspect memberAspect() {
        return new MemberAspect();
    }
}
