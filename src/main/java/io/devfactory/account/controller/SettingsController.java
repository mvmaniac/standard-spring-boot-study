package io.devfactory.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import io.devfactory.zone.domain.Zone;
import io.devfactory.zone.dto.ZoneFormView;
import io.devfactory.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static io.devfactory.account.controller.SettingsController.ROOT;
import static io.devfactory.account.controller.SettingsController.SETTINGS;
import static io.devfactory.global.utils.FunctionUtils.REDIRECT;

@RequiredArgsConstructor
@RequestMapping(ROOT + SETTINGS)
@Controller
public class SettingsController {

  static final String ROOT = "/";
  static final String VIEWS = "views/";
  static final String SETTINGS = "settings";
  static final String PROFILE = "/profile";
  static final String PASSWORD = "/password";
  static final String NOTIFICATIONS = "/notifications";
  static final String ACCOUNT = "/account";
  static final String TAGS = "/tags";
  static final String ZONES = "/zones";

  private final AccountService accountService;
  private final AccountRepository accountRepository;

  private final TagRepository tagRepository;
  private final ZoneRepository zoneRepository;

  private final ModelMapper modelMapper;
  private final ObjectMapper objectMapper;

  @InitBinder("passwordFormView")
  public void passwordFormViewInitBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(new PasswordFormViewValidator());
  }

  @InitBinder("nicknameFormView")
  public void nicknameFormViewInitBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(new NicknameFormViewValidator(accountRepository));
  }

  @GetMapping(PROFILE)
  public String viewProfileForm(@CurrentUser Account account, Model model) {
    model.addAttribute(account);
    model.addAttribute(modelMapper.map(account, ProfileFormView.class));
    return VIEWS + SETTINGS + PROFILE;
  }

  @PostMapping(PROFILE)
  public String modifyProfile(@CurrentUser Account account,
      @Valid @ModelAttribute ProfileFormView view, Errors errors, Model model,
      RedirectAttributes attributes) {

    if (errors.hasErrors()) {
      model.addAttribute(account);
      return VIEWS + SETTINGS + PROFILE;
    }

    accountService.updateProfile(account, view);

    attributes.addFlashAttribute("message", "프로필을 수정 했습니다.");
    return REDIRECT.apply("/" + SETTINGS + PROFILE);
  }

  @GetMapping(PASSWORD)
  public String viewPasswordForm(@CurrentUser Account account, Model model) {
    model.addAttribute(account);
    model.addAttribute(modelMapper.map(account, PasswordFormView.class));
    return VIEWS + SETTINGS + PASSWORD;
  }

  @PostMapping(PASSWORD)
  public String modifyPassword(@CurrentUser Account account, @Valid PasswordFormView view,
      Errors errors, Model model, RedirectAttributes attributes) {

    if (errors.hasErrors()) {
      model.addAttribute(account);
      return VIEWS + SETTINGS + PASSWORD;
    }

    accountService.updatePassword(account, view.getNewPassword());

    attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");
    return REDIRECT.apply("/" + SETTINGS + PASSWORD);
  }

  @GetMapping(NOTIFICATIONS)
  public String viewNotificationForm(@CurrentUser Account account, Model model) {
    model.addAttribute(account);
    model.addAttribute(modelMapper.map(account, NotificationFormView.class));
    return VIEWS + SETTINGS + NOTIFICATIONS;
  }

  @PostMapping(NOTIFICATIONS)
  public String modifyNotification(@CurrentUser Account account, @Valid NotificationFormView view,
      Errors errors, Model model, RedirectAttributes attributes) {

    if (errors.hasErrors()) {
      model.addAttribute(account);
      return VIEWS + SETTINGS + NOTIFICATIONS;
    }

    accountService.updateNotification(account, view);

    attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");
    return REDIRECT.apply("/" + SETTINGS + NOTIFICATIONS);
  }

  @GetMapping(ACCOUNT)
  public String viewAccountForm(@CurrentUser Account account, Model model) {
    model.addAttribute(account);
    model.addAttribute(modelMapper.map(account, NicknameFormView.class));
    return VIEWS + SETTINGS + ACCOUNT;
  }

  @PostMapping(ACCOUNT)
  public String modifyAccount(@CurrentUser Account account, @Valid NicknameFormView view,
      Errors errors, Model model, RedirectAttributes attributes) {
    if (errors.hasErrors()) {
      model.addAttribute(account);
      return VIEWS + SETTINGS + ACCOUNT;
    }

    accountService.updateNickname(account, view.getNickname());
    attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
    return REDIRECT.apply("/" + SETTINGS + ACCOUNT);
  }

  @GetMapping(TAGS)
  public String viewTagForm(@CurrentUser Account account, Model model)
      throws JsonProcessingException {
    model.addAttribute(account);

    final Set<Tag> tags = accountService.getTags(account);
    model.addAttribute("tags", tags.stream().map(Tag::getTitle).toList());

    // @formatter:off
    final List<String> allTags = tagRepository.findAll()
        .stream()
        .map(Tag::getTitle)
        .toList()
      ;
    // @formatter:on

    model.addAttribute("whiteList", objectMapper.writeValueAsString(allTags));

    return VIEWS + SETTINGS + TAGS;
  }

  @PostMapping(TAGS)
  public ResponseEntity<String> createTag(@CurrentUser Account account,
      @RequestBody TagFormView view) {
    String title = view.getTagTitle();

    Tag tag = tagRepository.findByTitle(title);

    if (Objects.isNull(tag)) {
      tag = tagRepository.save(Tag.of(title));
    }

    accountService.addTag(account, tag);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(TAGS)
  public ResponseEntity<String> removeTag(@CurrentUser Account account,
      @RequestBody TagFormView view) {
    String title = view.getTagTitle();

    Tag tag = tagRepository.findByTitle(title);

    if (Objects.isNull(tag)) {
      return ResponseEntity.badRequest().build();
    }

    accountService.removeTag(account, tag);
    return ResponseEntity.ok().build();
  }

  @GetMapping(ZONES)
  public String viewZoneForm(@CurrentUser Account account, Model model)
      throws JsonProcessingException {
    model.addAttribute(account);

    final Set<Zone> zones = accountService.getZones(account);
    model.addAttribute("zones", zones.stream().map(Zone::getZoneName).toList());

    // @formatter:off
    final List<String> allZones = zoneRepository.findAll()
        .stream()
        .map(Zone::getZoneName)
        .toList()
        ;
    // @formatter:on

    model.addAttribute("whiteList", objectMapper.writeValueAsString(allZones));

    return VIEWS + SETTINGS + ZONES;
  }

  @PostMapping(ZONES)
  public ResponseEntity<String> createZone(@CurrentUser Account account,
      @RequestBody ZoneFormView view) {
    final Zone zone = zoneRepository
        .findByCityAndProvince(view.getCityName(), view.getProvinceName());

    if (Objects.isNull(zone)) {
      return ResponseEntity.badRequest().build();
    }

    accountService.addZone(account, zone);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(ZONES)
  public ResponseEntity<String> removeZone(@CurrentUser Account account,
      @RequestBody ZoneFormView view) {
    final Zone zone = zoneRepository
        .findByCityAndProvince(view.getCityName(), view.getProvinceName());

    if (Objects.isNull(zone)) {
      return ResponseEntity.badRequest().build();
    }

    accountService.removeZone(account, zone);
    return ResponseEntity.ok().build();
  }

}
