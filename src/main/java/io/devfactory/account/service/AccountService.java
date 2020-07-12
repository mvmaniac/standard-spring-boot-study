package io.devfactory.account.service;

import io.devfactory.account.domain.Account;
import io.devfactory.account.dto.NotificationFormView;
import io.devfactory.account.dto.ProfileFormView;
import io.devfactory.account.dto.request.SignUpFormRequestView;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.global.config.security.service.UserAccount;
import io.devfactory.tag.domain.Tag;
import io.devfactory.zone.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final JavaMailSender javaMailSender;
  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;

  @Transactional
  public Account processSaveAccount(SignUpFormRequestView signUpFormRequestView) {
    final Account savedAccount = saveAccount(signUpFormRequestView);
    savedAccount.generateEmailCheckToken();
    sendSignUpConfirmEmail(savedAccount);
    return savedAccount;
  }

  public void sendSignUpConfirmEmail(Account savedAccount) {
    final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setTo(savedAccount.getEmail());
    simpleMailMessage.setSubject("스터디올래, 회원 가입 인증");
    simpleMailMessage.setText("/check-email-token?token=" + savedAccount.getEmailCheckToken() +
        "&email=" + savedAccount.getEmail());

    javaMailSender.send(simpleMailMessage);
  }

  @Transactional
  public void completeSingUp(Account findAccount) {
    findAccount.completeSingUp();
    login(findAccount);
  }

  public void login(Account loginAccount) {
    final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
        new UserAccount(loginAccount), loginAccount.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_USER")));

    final SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(token);
  }

  @Transactional
  public void updateProfile(Account account, ProfileFormView view) {
    modelMapper.map(view, account);
    accountRepository.save(account);
  }

  @Transactional
  public void updatePassword(Account account, String newPassword) {
    account.updatePassword(passwordEncoder.encode(newPassword));
    accountRepository.save(account);
  }

  @Transactional
  public void updateNotification(Account account, NotificationFormView view) {
    modelMapper.map(view, account);
    accountRepository.save(account);

  }

  @Transactional
  public void updateNickname(Account account, String nickname) {
    account.updateNickname(nickname);
    accountRepository.save(account);
    login(account); // 닉네임 변경을 위한 로그인 처리
  }

  @Transactional
  public void sendLoginLink(Account account) {
    account.generateEmailCheckToken();

    SimpleMailMessage mailMessage = new SimpleMailMessage();

    mailMessage.setTo(account.getEmail());
    mailMessage.setSubject("스터디올래, 로그인 링크");
    mailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken() +
        "&email=" + account.getEmail());

    javaMailSender.send(mailMessage);
  }

  public Set<Tag> getTags(Account account) {
    final Optional<Account> findAccount = accountRepository.findById(account.getId());
    return findAccount.orElseThrow().getTags();
  }

  @Transactional
  public void addTag(Account account, Tag tag) {
    final Optional<Account> findAccount = accountRepository.findById(account.getId());
    findAccount.ifPresent(a -> a.getTags().add(tag));
  }

  @Transactional
  public void removeTag(Account account, Tag tag) {
    final Optional<Account> findAccount = accountRepository.findById(account.getId());
    findAccount.ifPresent(a -> a.getTags().remove(tag));
  }

  public Set<Zone> getZones(Account account) {
    final Optional<Account> findAccount = accountRepository.findById(account.getId());
    return findAccount.orElseThrow().getZones();
  }

  @Transactional
  public void addZone(Account account, Zone zone) {
    final Optional<Account> findAccount = accountRepository.findById(account.getId());
    findAccount.ifPresent(a -> a.getZones().add(zone));
  }

  @Transactional
  public void removeZone(Account account, Zone zone) {
    final Optional<Account> findAccount = accountRepository.findById(account.getId());
    findAccount.ifPresent(a -> a.getZones().remove(zone));
  }

  private Account saveAccount(@Valid SignUpFormRequestView signUpFormRequestView) {
    final Account account = Account.of(signUpFormRequestView.getEmail(),
        signUpFormRequestView.getNickname(),
        passwordEncoder.encode(signUpFormRequestView.getPassword()));

    return accountRepository.save(account);
  }

}
