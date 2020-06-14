package io.devfactory.account.controller;

import io.devfactory.account.dto.request.SignUpFormRequestView;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;

@RequestMapping("/sign-up")
@Controller
public class AccountController {

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

    return REDIRECT.apply("/");
  }

}
