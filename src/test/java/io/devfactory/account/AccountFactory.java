package io.devfactory.account;

import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountFactory {

  @Autowired
  private AccountRepository accountRepository;

  public Account createAccount(String nickname) {
    final Account account = Account.create()
        .nickname(nickname)
        .email(nickname + "@gmail.com")
        .build();

    accountRepository.save(account);
    return account;
  }

}
