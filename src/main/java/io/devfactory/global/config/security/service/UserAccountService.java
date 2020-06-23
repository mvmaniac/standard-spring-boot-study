package io.devfactory.global.config.security.service;

import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class UserAccountService implements UserDetailsService {

  private final AccountRepository accountRepository;

  @Override
  public UserDetails loadUserByUsername(String emailOrNickname) {
    Account findAccount = accountRepository.findByEmail(emailOrNickname);

    if (Objects.isNull(findAccount)) {
      findAccount = accountRepository.findByNickname(emailOrNickname);
    }

    if (Objects.isNull(findAccount)) {
      throw new UsernameNotFoundException(emailOrNickname);
    }

    return new UserAccount(findAccount);
  }
}
