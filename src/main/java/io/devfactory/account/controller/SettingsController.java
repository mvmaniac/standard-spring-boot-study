package io.devfactory.account.controller;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;
import static java.util.stream.Collectors.toList;

import io.devfactory.account.domain.Account;
import io.devfactory.account.dto.NicknameFormView;
import io.devfactory.account.dto.NotificationFormView;
import io.devfactory.account.dto.PasswordFormView;
import io.devfactory.account.dto.ProfileFormView;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.account.service.AccountService;
import io.devfactory.account.validator.NicknameFormViewValidator;
import io.devfactory.account.validator.PasswordFormViewValidator;
import io.devfactory.global.config.security.service.CurrentUser;
import io.devfactory.tag.domain.Tag;
import io.devfactory.tag.dto.TagFormView;
import io.devfactory.tag.repository.TagRepository;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Controller
public class SettingsController {

  static final String SETTINGS_PROFILE_VIEW_NAME = "views/settings/profile";
  static final String SETTINGS_PROFILE_URL = "/settings/profile";

  static final String SETTINGS_PASSWORD_VIEW_NAME = "views/settings/password";
  static final String SETTINGS_PASSWORD_URL = "/settings/password";

  static final String SETTINGS_NOTIFICATION_VIEW_NAME = "views/settings/notification";
  static final String SETTINGS_NOTIFICATION_URL = "/settings/notifications";

  static final String SETTINGS_ACCOUNT_VIEW_NAME = "views/settings/account";
  static final String SETTINGS_ACCOUNT_URL = "/settings/account";

  static final String SETTINGS_TAG_VIEW_NAME = "views/settings/tag";
  static final String SETTINGS_TAG_URL = "/settings/tags";

  private final AccountService accountService;
  private final AccountRepository accountRepository;

  private final TagRepository tagRepository;

  private final ModelMapper modelMapper;

  @InitBinder("passwordFormView")
  public void passwordFormViewInitBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(new PasswordFormViewValidator());
  }

  @InitBinder("nicknameFormView")
  public void nicknameFormViewInitBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(new NicknameFormViewValidator(accountRepository));
  }

  @GetMapping(SETTINGS_PROFILE_URL)
  public String viewProfileForm(@CurrentUser Account account, Model model) {
    model.addAttribute(account);
    model.addAttribute(modelMapper.map(account, ProfileFormView.class));
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
    model.addAttribute(modelMapper.map(account, PasswordFormView.class));
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
  public String viewNotificationForm(@CurrentUser Account account, Model model) {
    model.addAttribute(account);
    model.addAttribute(modelMapper.map(account, NotificationFormView.class));
    return SETTINGS_NOTIFICATION_VIEW_NAME;
  }

  @PostMapping(SETTINGS_NOTIFICATION_URL)
  public String modifyNotification(@CurrentUser Account account, @Valid NotificationFormView view,
      Errors errors, Model model, RedirectAttributes attributes) {

    if (errors.hasErrors()) {
      model.addAttribute(account);
      return SETTINGS_NOTIFICATION_VIEW_NAME;
    }

    accountService.updateNotification(account, view);

    attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");
    return "redirect:" + SETTINGS_NOTIFICATION_URL;
  }

  @GetMapping(SETTINGS_ACCOUNT_URL)
  public String updateAccountForm(@CurrentUser Account account, Model model) {
    model.addAttribute(account);
    model.addAttribute(modelMapper.map(account, NicknameFormView.class));
    return SETTINGS_ACCOUNT_VIEW_NAME;
  }

  @PostMapping(SETTINGS_ACCOUNT_URL)
  public String updateAccount(@CurrentUser Account account, @Valid NicknameFormView view,
      Errors errors, Model model, RedirectAttributes attributes) {
    if (errors.hasErrors()) {
      model.addAttribute(account);
      return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    accountService.updateNickname(account, view.getNickname());
    attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
    return "redirect:" + SETTINGS_ACCOUNT_URL;
  }

  @GetMapping(SETTINGS_TAG_URL)
  public String viewTag(@CurrentUser Account account, Model model) {
    model.addAttribute(account);

    final Set<Tag> tags = accountService.getTags(account);
    model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(toList()));

    return SETTINGS_TAG_VIEW_NAME;
  }

  @PostMapping(SETTINGS_TAG_URL)
  public ResponseEntity<String> createTag(@CurrentUser Account account, @RequestBody TagFormView view) {
    String title = view.getTagTitle();

    Tag tag = tagRepository.findByTitle(title);

    if (Objects.isNull(tag)) {
      tag = tagRepository.save(Tag.of(title));
    }

    accountService.addTag(account, tag);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(SETTINGS_TAG_URL)
  public ResponseEntity<String> removeTag(@CurrentUser Account account, @RequestBody TagFormView view) {
    String title = view.getTagTitle();

    Tag tag = tagRepository.findByTitle(title);

    if (Objects.isNull(tag)) {
      return ResponseEntity.badRequest().build();
    }

    accountService.removeTag(account, tag);
    return ResponseEntity.ok().build();
  }

}
