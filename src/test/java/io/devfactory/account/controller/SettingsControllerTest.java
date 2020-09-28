package io.devfactory.account.controller;

import static io.devfactory.account.controller.SettingsController.ACCOUNT;
import static io.devfactory.account.controller.SettingsController.PASSWORD;
import static io.devfactory.account.controller.SettingsController.PROFILE;
import static io.devfactory.account.controller.SettingsController.ROOT;
import static io.devfactory.account.controller.SettingsController.SETTINGS;
import static io.devfactory.account.controller.SettingsController.TAGS;
import static io.devfactory.account.controller.SettingsController.VIEWS;
import static io.devfactory.account.controller.SettingsController.ZONES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.devfactory.account.WithAccount;
import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.account.service.AccountService;
import io.devfactory.infra.MockMvcTest;
import io.devfactory.tag.domain.Tag;
import io.devfactory.tag.dto.TagFormView;
import io.devfactory.tag.repository.TagRepository;
import io.devfactory.zone.domain.Zone;
import io.devfactory.zone.dto.ZoneFormView;
import io.devfactory.zone.repository.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@MockMvcTest
class SettingsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private ZoneRepository zoneRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ObjectMapper objectMapper;

  private Zone testZone = Zone.create().city("test").localNameOfCity("테스트시").province("테스트주")
      .build();

  @BeforeEach
  void beforeEach() {
    zoneRepository.save(testZone);
  }

  @AfterEach
  void afterEach() {
    accountRepository.deleteAll();
    zoneRepository.deleteAll();
  }

  @WithAccount("test")
  @DisplayName("프로필 수정폼")
  @Test
  void viewProfileForm() throws Exception {
    final String bio = "짧은 소개를 수정하는 경우";

    mockMvc
        .perform(get(ROOT + SETTINGS + PROFILE))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("profileFormView"))
    ;
  }

  @WithAccount("test")
  @DisplayName("프로필 수정하기 - 입력값 정상")
  @Test
  void updateProfile() throws Exception {
    final String bio = "짧은 소개를 수정하는 경우";

    mockMvc
        .perform(post(ROOT + SETTINGS + PROFILE)
            .param("bio", bio)
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(SETTINGS + PROFILE))
        .andExpect(flash().attributeExists("message"))
    ;

    final Account findAccount = accountRepository.findByNickname("test");
    assertEquals(bio, findAccount.getBio());
  }

  @WithAccount("test")
  @DisplayName("프로필 수정하기 - 입력값 에러")
  @Test
  void updateProfile_error() throws Exception {
    final String bio = "길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우";

    mockMvc
        .perform(post(ROOT + SETTINGS + PROFILE)
            .param("bio", bio)
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEWS + SETTINGS + PROFILE))
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("profileFormView"))
        .andExpect(model().hasErrors())
    ;

    final Account findAccount = accountRepository.findByNickname("test");
    assertNull(findAccount.getBio());
  }

  @WithAccount("test")
  @DisplayName("패스워드 수정 폼")
  @Test
  void viewPasswordForm() throws Exception {
    mockMvc
        .perform(get(ROOT + SETTINGS + PASSWORD))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("passwordFormView"));
  }

  @WithAccount("test")
  @DisplayName("패스워드 수정 - 입력값 정상")
  @Test
  void updatePassword_success() throws Exception {
    mockMvc
        .perform(post(ROOT + SETTINGS + PASSWORD)
            .param("newPassword", "12345678")
            .param("newPasswordConfirm", "12345678")
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(SETTINGS + PASSWORD))
        .andExpect(flash().attributeExists("message"));

    Account findAccount = accountRepository.findByNickname("test");
    assertTrue(passwordEncoder.matches("12345678", findAccount.getPassword()));
  }

  @WithAccount("test")
  @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
  @Test
  void updatePassword_fail() throws Exception {
    mockMvc
        .perform(post(ROOT + SETTINGS + PASSWORD)
            .param("newPassword", "12345678")
            .param("newPasswordConfirm", "11111111")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEWS + SETTINGS + PASSWORD))
        .andExpect(model().hasErrors())
        .andExpect(model().attributeExists("passwordFormView"))
        .andExpect(model().attributeExists("account"));
  }

  @WithAccount("test")
  @DisplayName("닉네임 수정 폼")
  @Test
  void viewAccountForm() throws Exception {
    mockMvc.perform(get(ROOT + SETTINGS + ACCOUNT))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("nicknameFormView"));
  }

  @WithAccount("test")
  @DisplayName("닉네임 수정하기 - 입력값 정상")
  @Test
  void updateAccount_success() throws Exception {
    String newNickname = "대표백수";

    mockMvc
        .perform(post(ROOT + SETTINGS + ACCOUNT)
            .param("nickname", newNickname)
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(SETTINGS + ACCOUNT))
        .andExpect(flash().attributeExists("message"));

    assertNotNull(accountRepository.findByNickname("대표백수"));
  }

  @WithAccount("test")
  @DisplayName("닉네임 수정하기 - 입력값 에러")
  @Test
  void updateAccount_fail() throws Exception {
    String newNickname = "¯\\_(ツ)_/¯";
    mockMvc
        .perform(post(ROOT + SETTINGS + ACCOUNT)
            .param("nickname", newNickname)
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEWS + SETTINGS + ACCOUNT))
        .andExpect(model().hasErrors())
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("nicknameFormView"));
  }

  @WithAccount("test")
  @DisplayName("태그 수정 폼")
  @Test
  void viewTagForm() throws Exception {
    mockMvc
        .perform(get(ROOT + SETTINGS + TAGS))
        .andExpect(view().name(VIEWS + SETTINGS + TAGS))
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("whiteList"))
        .andExpect(model().attributeExists("tags"))
    ;
  }

  @Transactional
  @WithAccount("test")
  @DisplayName("태그 추가")
  @Test
  void addTag() throws Exception {
    final String tagTitle = "newTag";
    final TagFormView view = TagFormView.of(tagTitle);

    mockMvc
        .perform(post(ROOT + SETTINGS + TAGS)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(view))
            .with(csrf()))
        .andExpect(status().isOk())
    ;

    final Tag findTag = tagRepository.findByTitle(tagTitle);
    assertNotNull(findTag);

    final Account findAccount = accountRepository.findByNickname("test");
    assertTrue(findAccount.getTags().contains(findTag));
  }

  @Transactional
  @WithAccount("test")
  @DisplayName("태그 삭제")
  @Test
  void removeTag() throws Exception {
    final Account findAccount = accountRepository.findByNickname("test");
    final String tagTitle = "newTag";
    final Tag newTag = tagRepository.save(Tag.of(tagTitle));

    accountService.addTag(findAccount, newTag);

    assertTrue(findAccount.getTags().contains(newTag));

    final TagFormView view = TagFormView.of(tagTitle);

    mockMvc
        .perform(delete(ROOT + SETTINGS + TAGS)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(view))
            .with(csrf()))
        .andExpect(status().isOk())
    ;

    assertFalse(findAccount.getTags().contains(newTag));
  }

  @WithAccount("test")
  @DisplayName("지역정보 수정 폼")
  @Test
  void viewZoneForm() throws Exception {
    mockMvc
        .perform(get(ROOT + SETTINGS + ZONES))
        .andExpect(view().name(VIEWS + SETTINGS + ZONES))
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("whiteList"))
        .andExpect(model().attributeExists("zones"))
    ;
  }

  @Transactional
  @WithAccount("test")
  @DisplayName("지역정보 추가")
  @Test
  void addZone() throws Exception {
    final ZoneFormView view = ZoneFormView.of(testZone.getZoneName());

    mockMvc
        .perform(post(ROOT + SETTINGS + ZONES)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(view))
            .with(csrf()))
        .andExpect(status().isOk())
    ;

    final Zone findZone = zoneRepository
        .findByCityAndProvince(testZone.getCity(), testZone.getProvince());
    assertNotNull(findZone);

    final Account findAccount = accountRepository.findByNickname("test");
    assertTrue(findAccount.getZones().contains(findZone));
  }

  @Transactional
  @WithAccount("test")
  @DisplayName("지역정보 삭제")
  @Test
  void removeZone() throws Exception {
    final Account findAccount = accountRepository.findByNickname("test");
    final Zone findZone = zoneRepository
        .findByCityAndProvince(testZone.getCity(), testZone.getProvince());
    accountService.addZone(findAccount, findZone);

    assertTrue(findAccount.getZones().contains(findZone));

    final ZoneFormView view = ZoneFormView.of(findZone.getZoneName());

    mockMvc
        .perform(delete(ROOT + SETTINGS + ZONES)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(view))
            .with(csrf()))
        .andExpect(status().isOk())
    ;

    assertFalse(findAccount.getZones().contains(findZone));
  }

}
