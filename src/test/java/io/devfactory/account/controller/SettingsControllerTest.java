package io.devfactory.account.controller;

import static io.devfactory.account.controller.SettingsController.SETTINGS_PROFILE_URL;
import static io.devfactory.account.controller.SettingsController.SETTINGS_PROFILE_VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import io.devfactory.WithAccount;
import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class SettingsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @AfterEach
  void afterEach() {
    accountRepository.deleteAll();
  }

  @WithAccount("test")
  @DisplayName("프로필 수정폼")
  @Test
  void viewProfileForm() throws Exception {
    final String bio = "짧은 소개를 수정하는 경우";

    mockMvc
        .perform(get(SETTINGS_PROFILE_URL))
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
        .perform(post(SETTINGS_PROFILE_URL)
            .param("bio", bio)
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(SETTINGS_PROFILE_URL))
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
        .perform(post(SETTINGS_PROFILE_URL)
            .param("bio", bio)
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(SETTINGS_PROFILE_VIEW_NAME))
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
        .perform(get(SettingsController.SETTINGS_PASSWORD_URL))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("passwordFormView"));
  }

  @WithAccount("test")
  @DisplayName("패스워드 수정 - 입력값 정상")
  @Test
  void updatePassword_success() throws Exception {
    mockMvc
        .perform(post(SettingsController.SETTINGS_PASSWORD_URL)
            .param("newPassword", "12345678")
            .param("newPasswordConfirm", "12345678")
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
        .andExpect(flash().attributeExists("message"));

    Account findAccount = accountRepository.findByNickname("test");
    assertTrue(passwordEncoder.matches("12345678", findAccount.getPassword()));
  }

  @WithAccount("test")
  @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
  @Test
  void updatePassword_fail() throws Exception {
    mockMvc
        .perform(post(SettingsController.SETTINGS_PASSWORD_URL)
            .param("newPassword", "12345678")
            .param("newPasswordConfirm", "11111111")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
        .andExpect(model().hasErrors())
        .andExpect(model().attributeExists("passwordFormView"))
        .andExpect(model().attributeExists("account"));
  }

  @WithAccount("test")
  @DisplayName("닉네임 수정 폼")
  @Test
  void viewAccountForm() throws Exception {
    mockMvc.perform(get(SettingsController.SETTINGS_ACCOUNT_URL))
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
        .perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
            .param("nickname", newNickname)
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(SettingsController.SETTINGS_ACCOUNT_URL))
        .andExpect(flash().attributeExists("message"));

    assertNotNull(accountRepository.findByNickname("대표백수"));
  }

  @WithAccount("test")
  @DisplayName("닉네임 수정하기 - 입력값 에러")
  @Test
  void updateAccount_fail() throws Exception {
    String newNickname = "¯\\_(ツ)_/¯";
    mockMvc
        .perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
            .param("nickname", newNickname)
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
        .andExpect(model().hasErrors())
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("nicknameFormView"));
  }

}
