package io.devfactory.global.config.security;

import io.devfactory.global.config.security.service.UserAccountService;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.header.writers.frameoptions.WhiteListedAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserAccountService userAccountService;
  private final DataSource dataSource;

  @Value("${spring.profiles.active:default}")
  private String activeProfile;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
      .authorizeRequests()
        .antMatchers("/h2-console/**", "/", "/sign-up", "/check-email-token", "/email-login", "/login-by-email")
          .permitAll()
        .antMatchers(HttpMethod.GET, "/profile/*")
          .permitAll()
        .anyRequest()
          .authenticated()
        .and()

      .formLogin()
        .loginPage("/login")
          .permitAll()
        .and()

      .logout()
        .logoutSuccessUrl("/")
        .and()

      .rememberMe()
        .userDetailsService(userAccountService)
        .tokenRepository(tokenRepository())
    // @formatter:on
    ;

    // 개발 환경용 설정
    if (List.of("default", "local").contains(activeProfile)) {
      // @formatter:off
      http
        .headers()
          .addHeaderWriter(new XFrameOptionsHeaderWriter(new WhiteListedAllowFromStrategy(Arrays.asList("localhost"))))
          .frameOptions()
          .sameOrigin()

        .and()
          .csrf()
            .ignoringAntMatchers("/h2-console/**")
        ;
      // @formatter:on
    }
  }

  @Bean
  public PersistentTokenRepository tokenRepository() {
    final JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
    jdbcTokenRepository.setDataSource(dataSource);
    return jdbcTokenRepository;
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
        .antMatchers("/node_modules/**")
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
  }

}
