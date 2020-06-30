package io.devfactory.account.controller;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;

import io.devfactory.account.domain.Account;
import io.devfactory.account.dto.NotificationFormView;
import io.devfactory.account.dto.PasswordFormView;
import io.devfactory.account.dto.ProfileFormView;
import io.devfactory.account.service.AccountService;
import io.devfactory.account.validator.PasswordFormViewValidator;
import io.devfactory.global.config.security.service.CurrentUser;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class SettingsController {

  static final String SETTINGS_PROFILE_VIEW_NAME = "views/settings/profile";
  static final String SETTINGS_PROFILE_URL = "/settings/profile";

  static final String SETTINGS_PASSWORD_VIEW_NAME = "views/settings/password";
  static final String SETTINGS_PASSWORD_URL = "/settings/password";

  static final String SETTINGS_NOTIFICATION_VIEW_NAME = "views/settings/notification";
  static final String SETTINGS_NOTIFICATION_URL = "/settings/notifications";

  private final AccountService accountService;

  @InitBinder("passwordFormView")
  public void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(new PasswordFormViewValidator());
  }

  @GetMapping(SETTINGS_PROFILE_URL)
  public String viewProfileForm(@CurrentUser Account account, Model model) {
    model.addAttribute(account);
    model.addAttribute(ProfileFormView.of(account));
    return SETTINGS_PROFILE_VIEW_NAME;
  }

  @PostMapping(SETTINGS_PROFILE_URL)
  public String modifyProfile(@CurrentUser Account account,
      @Valid @ModelAttribute ProfileFormView view, Errors errors, Model model,
      RedirectAttributes attributes) {

    if (errors.hasErrors()) {
      model.addAttribute(account);
      return SETTINGS_PROFILE_VIEW_NAME;
    }

    accountService.updateProfile(account, view);

    attributes.addFlashAttribute("message", "프로필을 수정 했습니다.");
    return REDIRECT.apply(SETTINGS_PROFILE_URL);
  }

  @GetMapping(SETTINGS_PASSWORD_URL)
  public String viewPasswordForm(@CurrentUser Account account, Model model) {
    model.addAttribute(account);
    model.addAttribute(PasswordFormView.of());
    return SETTINGS_PASSWORD_VIEW_NAME;
  }

  @PostMapping(SETTINGS_PASSWORD_URL)
  public String modifyPassword(@CurrentUser Account account, @Valid PasswordFormView view,
      Errors errors, Model model, RedirectAttributes attributes) {

    if (errors.hasErrors()) {
      model.addAttribute(account);
      return SETTINGS_PASSWORD_VIEW_NAME;
    }

    accountService.updatePassword(account, view.getNewPassword());

    attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");
    return REDIRECT.apply(SETTINGS_PASSWORD_URL);
  }

  @GetMapping(SETTINGS_NOTIFICATION_URL)
  public String updateNotificationsForm(@CurrentUser Account account, Model model) {
    model.addAttribute(account);
    model.addAttribute(NotificationFormView.of(account));
    return SETTINGS_NOTIFICATION_VIEW_NAME;
  }

  @PostMapping(SETTINGS_NOTIFICATION_URL)
  public String updateNotifications(@CurrentUser Account account, @Valid NotificationFormView view,
      Errors errors, Model model, RedirectAttributes attributes) {

    if (errors.hasErrors()) {
      model.addAttribute(account);
      return SETTINGS_NOTIFICATION_VIEW_NAME;
    }

    accountService.updateNotification(account, view);

    attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");
    return "redirect:" + SETTINGS_NOTIFICATION_URL;
  }

}
