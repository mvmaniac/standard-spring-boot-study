package io.devfactory.account.controller;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;

import io.devfactory.account.domain.Account;
import io.devfactory.account.dto.request.SignUpFormRequestView;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.account.service.AccountService;
import io.devfactory.account.validator.SignUpFormRequestViewValidator;
import java.util.Objects;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class AccountController {

  private final SignUpFormRequestViewValidator signUpFormRequestViewValidator;

  private final AccountService accountService;
  private final AccountRepository accountRepository;

  @InitBinder("/sign-up/signUpFormRequestView")
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

    findAccount.completeSingUp();

    accountService.login(findAccount);

    model.addAttribute("numberOfUser", accountRepository.count());
    model.addAttribute("nickname", findAccount.getNickname());
    return viewName;
  }

}
