package io.devfactory.global.config.security;

import io.devfactory.global.config.security.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import javax.sql.DataSource;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserAccountService userAccountService;
  private final DataSource dataSource;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
      .authorizeRequests()
        .antMatchers("/", "/sign-up", "/check-email-token", "/check-email-login", "/email-login", "/login-link")
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
