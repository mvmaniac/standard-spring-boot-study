package io.devfactory.account.repository;

import io.devfactory.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);

  Account findByEmail(String email);

  Account findByNickname(String nickname);

}
