package shop.server.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import shop.server.auth.handler.MemberAccessDeniedHandler;
import shop.server.auth.handler.MemberAuthenticationFailHandler;
import shop.server.auth.handler.MemberLogOutSuccessHandler;
import shop.server.auth.memberdetails.MemberDetailsService;
import shop.server.config.SecurityConfig;

@TestConfiguration
@EnableWebSecurity
public class TestConfig {

    @PersistenceContext
    private EntityManager em;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(HttpBasicConfigurer::disable)
                .authorizeHttpRequests(ahr -> ahr
                        .requestMatchers(HttpMethod.GET,"/myShop/home").permitAll()
                        .requestMatchers(HttpMethod.GET, "/jwt/newToken").permitAll()
                        .requestMatchers(HttpMethod.POST, "/myShop/member/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/myShop/member/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/myShop/member/info/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/myShop/member/addPoint/*/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/myShop/member/update/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/myShop/member/delete/*").authenticated())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

}
