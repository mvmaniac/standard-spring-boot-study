package io.devfactory;

import io.devfactory.account.dto.request.SignUpFormRequestView;
import io.devfactory.account.service.AccountService;
import io.devfactory.global.config.security.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

  private final UserAccountService userAccountService;
  private final AccountService accountService;

  @Override
  public SecurityContext createSecurityContext(WithAccount withAccount) {
    String nickname = withAccount.value();

    final SignUpFormRequestView signUpFormRequestView = SignUpFormRequestView.create()
        .email(nickname + "@gmail.com")
        .nickname(nickname)
        .password("12345678")
        .build();

    accountService.processSaveAccount(signUpFormRequestView);

    UserDetails principal = userAccountService.loadUserByUsername(nickname);

    Authentication authentication = new UsernamePasswordAuthenticationToken(principal,
        principal.getPassword(), principal.getAuthorities());

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);

    return context;
  }

}
