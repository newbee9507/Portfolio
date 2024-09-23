package shop.server.config;

import org.springframework.security.test.context.support.WithSecurityContext;
import shop.server.item.entity.Item;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = SecurityContextForTest.class)
public @interface UserDetailsForTest {
    long memberId() default 1L;
    String id() default "admin";
    String nickName() default "admin";
    int point() default 0;
    String[] roles() default "ADMIN";
}
