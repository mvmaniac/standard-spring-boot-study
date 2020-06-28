package io.devfactory.account.controller;

import static io.devfactory.account.controller.SettingsController.SETTINGS_PROFILE_URL;
import static io.devfactory.account.controller.SettingsController.SETTINGS_PROFILE_VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.devfactory.WithAccount;
import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class SettingsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AccountRepository accountRepository;

  @AfterEach
  void afterEach() {
    accountRepository.deleteAll();
  }

  @WithAccount("dev")
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

  @WithAccount("dev")
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

    final Account findAccount = accountRepository.findByNickname("dev");
    assertEquals(bio, findAccount.getBio());
  }

  @WithAccount("dev")
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

    final Account findAccount = accountRepository.findByNickname("dev");
    assertNull(findAccount.getBio());
  }

}
