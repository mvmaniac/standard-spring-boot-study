package io.devfactory.account.service;

import io.devfactory.account.domain.Account;
import io.devfactory.account.dto.request.SignUpFormRequestView;
import io.devfactory.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.validation.Valid;

@RequiredArgsConstructor
@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final JavaMailSender javaMailSender;
  private final PasswordEncoder passwordEncoder;

  public void processSaveAccount(SignUpFormRequestView signUpFormRequestView) {
    final Account savedAccount = saveAccount(signUpFormRequestView);
    savedAccount.generateEmailCheckToken();
    sendSignUpConfirmEmail(savedAccount);
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
        "&email" + savedAccount.getEmail());

    javaMailSender.send(simpleMailMessage);
  }


}
