package io.devfactory.account.controller;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;

import io.devfactory.account.domain.Account;
import io.devfactory.account.dto.request.SignUpFormRequestView;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.account.service.AccountService;
import io.devfactory.account.validator.SignUpFormRequestViewValidator;
import java.util.Objects;
import javax.validation.Valid;
import io.devfactory.global.config.security.service.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class AccountController {

  private final SignUpFormRequestViewValidator signUpFormRequestViewValidator;

  private final AccountService accountService;
  private final AccountRepository accountRepository;

  @InitBinder("signUpFormRequestView")
  public void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(signUpFormRequestViewValidator);
  }

  @GetMapping("/sign-up")
  public String viewSignUpForm(Model model) {
    model.addAttribute(SignUpFormRequestView.create().build());
    return "views/account/signUp";
  }

  @PostMapping("/sign-up")
  public String signUp(@Valid SignUpFormRequestView signUpFormRequestView, Errors errors) {
    if (errors.hasErrors()) {
      return "views/account/signUp";
    }

    final Account savedAccount = accountService.processSaveAccount(signUpFormRequestView);
    accountService.login(savedAccount);

    return REDIRECT.apply("/");
  }

  @SuppressWarnings("squid:S3516")
  @GetMapping("/check-email-token")
  public String checkEmailToken(String token, String email, Model model) {
    final Account findAccount = accountRepository.findByEmail(email);
    final String viewName = "views/account/checkedEmail";

    if (Objects.isNull(findAccount)) {
      model.addAttribute("error", "wrong.email");
      return viewName;
    }

    if (!findAccount.isValidToken(token)) {
      model.addAttribute("error", "wrong.token");
      return viewName;
    }

    accountService.completeSingUp(findAccount);

    model.addAttribute("numberOfUser", accountRepository.count());
    model.addAttribute("nickname", findAccount.getNickname());
    return viewName;
  }

  @GetMapping("/check-email")
  public String checkEmail(@CurrentUser Account account, Model model) {
    model.addAttribute("email", account.getEmail());
    return "views/account/checkEmail";
  }

  @GetMapping("/resend-confirm-email")
  public String resendConfirmEmail(@CurrentUser Account account, Model model) {
    if (!account.canSendConfirmEmail()) {
      model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
      model.addAttribute("email", account.getEmail());
      return "views/account/checkEmail";
    }

    accountService.sendSignUpConfirmEmail(account);
    return  REDIRECT.apply("/");
  }

  @GetMapping("/profile/{nickname}")
  public String viewProfile(@PathVariable String nickname, Model model, @CurrentUser Account account) {
    final Account findAccount = accountRepository.findByNickname(nickname);

    if (Objects.isNull(nickname)) {
      throw new IllegalArgumentException(nickname +"에 해당하는 사용자가 없습니다.");
    }

    model.addAttribute(findAccount); // 해당하는 타입의 케멀케이스로 들어감 (Account 타입으로 account 라는 이름으로 들어감)
    model.addAttribute("isOwner", findAccount.equals(account));

    return "views/account/profile";
  }

}
