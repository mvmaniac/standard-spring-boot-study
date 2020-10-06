package io.devfactory.study.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import io.devfactory.account.AccountFactory;
import io.devfactory.account.WithAccount;
import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.infra.MockMvcTest;
import io.devfactory.study.StudyFactory;
import io.devfactory.study.domain.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@MockMvcTest
class StudySettingsControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  private StudyFactory studyFactory;

  @Autowired
  private AccountFactory accountFactory;

  @Autowired
  private AccountRepository accountRepository;

  @WithAccount("test")
  @DisplayName("스터디 소개 수정 폼 조회 - 실패 (권한 없는 유저)")
  @Test
  void updateDescriptionForm_fail() throws Exception {
    Account subtest = accountFactory.createAccount("subtest");
    Study study = studyFactory.createStudy("test-study", subtest);

    mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
        .andExpect(status().isOk())
        .andExpect(view().name("error/403"));
  }

  @WithAccount("test")
  @DisplayName("스터디 소개 수정 폼 조회 - 성공")
  @Test
  void updateDescriptionForm_success() throws Exception {
    Account test = accountRepository.findByNickname("test");
    Study study = studyFactory.createStudy("test-study", test);

    mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
        .andExpect(status().isOk())
        .andExpect(view().name("views/study/settings/description"))
        .andExpect(model().attributeExists("studyDescriptionFormView"))
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("study"));
  }

  @WithAccount("test")
  @DisplayName("스터디 소개 수정 - 성공")
  @Test
  void updateDescription_success() throws Exception {
    Account test = accountRepository.findByNickname("test");
    Study study = studyFactory.createStudy("test-study", test);

    String settingsDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
    mockMvc.perform(post(settingsDescriptionUrl)
        .param("shortDescription", "short description")
        .param("fullDescription", "full description")
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(settingsDescriptionUrl))
        .andExpect(flash().attributeExists("message"));
  }

  @WithAccount("test")
  @DisplayName("스터디 소개 수정 - 실패")
  @Test
  void updateDescription_fail() throws Exception {
    Account test = accountRepository.findByNickname("test");
    Study study = studyFactory.createStudy("test-study", test);

    String settingsDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
    mockMvc.perform(post(settingsDescriptionUrl)
        .param("shortDescription", "")
        .param("fullDescription", "full description")
        .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(model().hasErrors())
        .andExpect(model().attributeExists("studyDescriptionFormView"))
        .andExpect(model().attributeExists("study"))
        .andExpect(model().attributeExists("account"));
  }

}
