package io.devfactory.account.controller;

import io.devfactory.account.domain.Account;
import io.devfactory.account.dto.ProfileFormView;
import io.devfactory.account.service.AccountService;
import io.devfactory.global.config.security.service.CurrentUser;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;

@RequiredArgsConstructor
@Controller
public class SettingsController {

  static final String SETTINGS_PROFILE_VIEW_NAME = "views/settings/profile";
  static final String SETTINGS_PROFILE_URL = "/settings/profile";

  private final AccountService accountService;

  @GetMapping(SETTINGS_PROFILE_URL)
  public String viewProfileForm(@CurrentUser Account account, Model model) {
    model.addAttribute(account);
    model.addAttribute(ProfileFormView.of(account));
    return SETTINGS_PROFILE_VIEW_NAME;
  }

  @PostMapping(SETTINGS_PROFILE_URL)
  public String modifyProfile(@CurrentUser Account account,
      @Valid @ModelAttribute ProfileFormView view, Errors errors, Model model, RedirectAttributes attributes) {

    if (errors.hasErrors()) {
      model.addAttribute(account);
      return SETTINGS_PROFILE_VIEW_NAME;
    }

    accountService.updateProfile(account, view);

    attributes.addFlashAttribute("message", "프로필을 수정 했습니다.");
    return REDIRECT.apply(SETTINGS_PROFILE_URL);
  }

}
