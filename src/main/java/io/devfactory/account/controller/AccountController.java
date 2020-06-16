package io.devfactory.account.controller;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;

import io.devfactory.account.dto.request.SignUpFormRequestView;
import io.devfactory.account.service.AccountService;
import io.devfactory.account.validator.SignUpFormRequestViewValidator;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/sign-up")
@Controller
public class AccountController {

  private final SignUpFormRequestViewValidator signUpFormRequestViewValidator;
  private final AccountService accountService;

  @InitBinder("signUpFormRequestView")
  public void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(signUpFormRequestViewValidator);
  }

  @GetMapping
  public String viewSignUpForm(Model model) {
    model.addAttribute(SignUpFormRequestView.create().build());
    return "views/account/signUp";
  }

  @PostMapping
  public String signUp(@Valid SignUpFormRequestView signUpFormRequestView, Errors errors) {
    if (errors.hasErrors()) {
      return "views/account/signUp";
    }

    accountService.processSaveAccount(signUpFormRequestView);
    return REDIRECT.apply("/");
  }

}
