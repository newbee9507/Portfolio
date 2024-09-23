package shop.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shop.server.auth.JwtToken.RefreshToken.JwtTokenController;
import shop.server.auth.JwtToken.RefreshToken.RefreshTokenRepository;
import shop.server.auth.authorization.MemberAuthorityUtils;
import shop.server.auth.filter.JwtAuthenticationFilter;
import shop.server.auth.filter.JwtVerificationFilter;
import shop.server.auth.handler.*;
import shop.server.auth.memberdetails.MemberDetailsService;
import shop.server.auth.JwtToken.JwtTokenizer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableConfigurationProperties(JwtTokenizer.class)
public class SecurityConfig {

    private final JwtTokenizer tokenizer;
    private final MemberAuthorityUtils memberAuthorityUtils;
    private final MemberDetailsService memberDetailsService;
    private final JwtTokenController jwtTokenController;
    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(CorsConfigurer -> CorsConfigurer.configurationSource(corsConfig()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(HttpBasicConfigurer::disable)
                .with(new JwtFilterConfig(), JwtFilterConfig::build)
                .logout(lc -> lc.logoutUrl("/myShop/member/logout")
                        .addLogoutHandler(new MemberLogOutSuccessHandler(refreshTokenRepository)))
                .exceptionHandling(eh -> eh.authenticationEntryPoint(new MemberAuthenticationFailHandler())
                        .accessDeniedHandler(new MemberAccessDeniedHandler()))
//                        .accessDeniedPage("/myShop/home"))
                .authorizeHttpRequests(ahr -> ahr
                        .requestMatchers(HttpMethod.GET, "/myShop/home").permitAll()
                        .requestMatchers(HttpMethod.GET, "/jwt/newToken").permitAll()
                        .requestMatchers(HttpMethod.POST, "/myShop/member/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/myShop/member/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/myShop/member/test/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/myShop/member/info/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/myShop/member/update/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/myShop/member/addPoint/*/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/myShop/member/delete/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/myShop/item/info/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/myShop/item/find/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/myShop/item/regist").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/myShop/item/update/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/myShop/item/delete/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/myShop/order/info/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/myShop/order/register").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/myShop/order/arriveOrder/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/myShop/order/delete/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/myShop/basket/info").authenticated()
                        .requestMatchers(HttpMethod.POST, "/myShop/basket/add/*/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/myShop/basket/modifyQuantity/*/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/myShop/basket/clear").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/myShop/basket/delete/*").authenticated())
                .build();
    }

    protected CorsConfigurationSource corsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "expirationTime"));
        corsConfiguration.setAllowedMethods(List.of(
                HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.DELETE.name(), HttpMethod.PATCH.name(),
                HttpMethod.OPTIONS.name(), HttpMethod.PUT.name()));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:8080", "https://localhost:8080"));
        corsConfiguration.setExposedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    public class JwtFilterConfig extends AbstractHttpConfigurer<JwtFilterConfig, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {

            AuthenticationManager authenticationManager
                    = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter
                    = new JwtAuthenticationFilter(authenticationManager, tokenizer, jwtTokenController);
            jwtAuthenticationFilter.setFilterProcessesUrl("/myShop/member/login");

            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberLoginSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberLoginFailureHandler());

            JwtVerificationFilter jwtVerificationFilter
                    = new JwtVerificationFilter(tokenizer, memberAuthorityUtils, memberDetailsService);

            builder.addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }

        public HttpSecurity build() {
            return getBuilder();
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
