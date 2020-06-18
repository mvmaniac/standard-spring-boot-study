package io.devfactory.account.service;

import io.devfactory.account.domain.Account;
import io.devfactory.account.dto.request.SignUpFormRequestView;
import io.devfactory.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final JavaMailSender javaMailSender;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public Account processSaveAccount(SignUpFormRequestView signUpFormRequestView) {
    final Account savedAccount = saveAccount(signUpFormRequestView);
    savedAccount.generateEmailCheckToken();
    sendSignUpConfirmEmail(savedAccount);
    return savedAccount;
  }

  private Account saveAccount(@Valid SignUpFormRequestView signUpFormRequestView) {
    // TODO: encoding 해야 함
    final Account account = Account.of(signUpFormRequestView.getEmail(),
        signUpFormRequestView.getNickname(),
        passwordEncoder.encode(signUpFormRequestView.getPassword()));

    return accountRepository.save(account);
  }

  private void sendSignUpConfirmEmail(Account savedAccount) {
    final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setTo(savedAccount.getEmail());
    simpleMailMessage.setSubject("스터디올래, 회원 가입 인증");
    simpleMailMessage.setText("/check-email-token?token=" + savedAccount.getEmailCheckToken() +
        "&email=" + savedAccount.getEmail());

    javaMailSender.send(simpleMailMessage);
  }

  public void login(Account loginAccount) {
    final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
        loginAccount.getNickname(), loginAccount.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_USER")));

    final SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(token);
  }

}
