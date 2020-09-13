package io.devfactory.study.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import io.devfactory.account.AccountFactory;
import io.devfactory.account.WithAccount;
import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.infra.AbstractContainerBaseTest;
import io.devfactory.infra.MockMvcTest;
import io.devfactory.study.StudyFactory;
import io.devfactory.study.domain.Study;
import io.devfactory.study.repository.StudyRepository;
import io.devfactory.study.service.StudyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@MockMvcTest
class StudyControllerTest extends AbstractContainerBaseTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  private StudyFactory studyFactory;

  @Autowired
  private AccountFactory accountFactory;

  @Autowired
  protected StudyService studyService;

  @Autowired
  protected StudyRepository studyRepository;

  @Autowired
  protected AccountRepository accountRepository;

  @WithAccount("test")
  @DisplayName("스터디 개설 폼 조회")
  @Test
  void createStudyForm() throws Exception {
    mockMvc.perform(get("/study"))
        .andExpect(status().isOk())
        .andExpect(view().name("views/study/form"))
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("studyFormView"));
  }


  @WithAccount("test")
  @DisplayName("스터디 개설 - 완료")
  @Test
  void createStudy_success() throws Exception {
    mockMvc.perform(post("/study")
        .param("path", "test-path")
        .param("title", "study title")
        .param("shortDescription", "short description of a study")
        .param("fullDescription", "full description of a study")
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/test-path"));

    Study study = studyRepository.findByPath("test-path");
    assertNotNull(study);
    Account account = accountRepository.findByNickname("test");
    assertTrue(study.getManagers().contains(account));
  }

  @WithAccount("test")
  @DisplayName("스터디 개설 - 실패")
  @Test
  void createStudy_fail() throws Exception {
    mockMvc.perform(post("/study")
        .param("path", "wrong path")
        .param("title", "study title")
        .param("shortDescription", "short description of a study")
        .param("fullDescription", "full description of a study")
        .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("views/study/form"))
        .andExpect(model().hasErrors())
        .andExpect(model().attributeExists("studyFormView"))
        .andExpect(model().attributeExists("account"));

    Study study = studyRepository.findByPath("test-path");
    assertNull(study);
  }

  @WithAccount("test")
  @DisplayName("스터디 조회")
  @Test
  void viewStudy() throws Exception {
    Study study = Study.create()
        .path("test-path")
        .title("test study")
        .shortDescription("short description")
        .fullDescription("<p>full description</p>")
        .build();

    Account findAccount = accountRepository.findByNickname("test");
    studyService.saveStudy(study, findAccount);

    mockMvc.perform(get("/study/test-path"))
        .andExpect(view().name("views/study/view"))
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("study"));
  }

  @WithAccount("test")
  @DisplayName("스터디 가입")
  @Test
  void joinStudy() throws Exception {
    Account subtest = accountFactory.createAccount("subtest");

    Study study = studyFactory.createStudy("test-study", subtest);

    mockMvc.perform(get("/study/" + study.getPath() + "/join"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

    Account findAccount = accountRepository.findByNickname("test");
    assertTrue(study.getMembers().contains(findAccount));
  }

  @WithAccount("test")
  @DisplayName("스터디 탈퇴")
  @Test
  void leaveStudy() throws Exception {
    Account subtest = accountFactory.createAccount("subtest");
    Study study = studyFactory.createStudy("test-study", subtest);

    Account findAccount = accountRepository.findByNickname("test");
    studyService.addMember(study, findAccount);

    mockMvc.perform(get("/study/" + study.getPath() + "/leave"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

    assertFalse(study.getMembers().contains(findAccount));
  }

}
