package io.devfactory.global.config.security;

import io.devfactory.global.config.security.service.UserAccountService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

  private final UserAccountService userAccountService;
  private final DataSource dataSource;

  @Value("${spring.profiles.active:default}")
  private String activeProfile;

  @Order(0)
  @Bean
  public SecurityFilterChain resourceChain(HttpSecurity http) throws Exception {
    // @formatter:off
    return http
      .securityMatchers(matcher -> matcher.requestMatchers(StaticResource.getResources("/node_modules/**")))
      .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
      .requestCache(RequestCacheConfigurer::disable)
      .securityContext(AbstractHttpConfigurer::disable)
      .sessionManagement(AbstractHttpConfigurer::disable)
      .build();
    // @formatter:on
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    final var httpSecurity = http
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers("/h2-console/**", "/", "/sign-up", "/check-email-token", "/email-login", "/login-by-email", "/search/study")
          .permitAll()
        .requestMatchers(HttpMethod.GET, "/profile/*")
          .permitAll()
        .anyRequest()
          .authenticated())
      .formLogin(form -> form.loginPage("/login").permitAll())
      .logout(logout -> logout.logoutSuccessUrl("/"))
      .rememberMe(remember -> remember.userDetailsService(userAccountService).tokenRepository(tokenRepository()));

    if (List.of("default", "local").contains(activeProfile)) {
      // @formatter:off
      httpSecurity
        .headers(header -> header.frameOptions().sameOrigin())
        .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
      // @formatter:on
    }

    return httpSecurity.build();
    // @formatter:on
  }

  @Bean
  public PersistentTokenRepository tokenRepository() {
    final JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
    jdbcTokenRepository.setDataSource(dataSource);
    return jdbcTokenRepository;
  }

  @Getter
  public static class StaticResource {

    private StaticResource() {
      throw new IllegalStateException("Constructor not supported");
    }

    private static final String[] defaultResources = Arrays.stream(StaticResourceLocation.values())
        .flatMap(StaticResourceLocation::getPatterns)
        .toArray(String[]::new);

    public static String[] getResources() {
      return defaultResources;
    }

    public static String[] getResources(String... antPatterns) {
      final var defaultResources = StaticResource.defaultResources;
      final var resources = Arrays.copyOf(defaultResources, defaultResources.length + antPatterns.length);
      System.arraycopy(antPatterns, 0, resources, defaultResources.length, antPatterns.length);
      return resources;
    }

  }

}
