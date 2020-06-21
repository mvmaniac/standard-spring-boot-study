package io.devfactory.global.config.security.service;

import io.devfactory.account.domain.Account;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class UserAccount extends User {

  private Account account;

  public UserAccount(Account account) {
    super(account.getNickname(), account.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_USER")));
    this.account = account;
  }

}
