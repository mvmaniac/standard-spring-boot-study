package io.devfactory.study.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import io.devfactory.WithAccount;
import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.study.domain.Study;
import io.devfactory.study.repository.StudyRepository;
import io.devfactory.study.service.StudyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class StudyControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected StudyService studyService;

  @Autowired
  protected StudyRepository studyRepository;

  @Autowired
  protected AccountRepository accountRepository;

  @Test
  @WithAccount("test")
  @DisplayName("스터디 개설 폼 조회")
  void createStudyForm() throws Exception {
    mockMvc.perform(get("/study"))
        .andExpect(status().isOk())
        .andExpect(view().name("views/study/form"))
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("studyFormView"));
  }


  @Test
  @WithAccount("test")
  @DisplayName("스터디 개설 - 완료")
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

  @Test
  @WithAccount("test")
  @DisplayName("스터디 개설 - 실패")
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

  @Test
  @WithAccount("test")
  @DisplayName("스터디 조회")
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

  @Test
  @WithAccount("test")
  @DisplayName("스터디 가입")
  void joinStudy() throws Exception {
    Account subtest = createAccount("subtest");

    Study study = createStudy("test-study", subtest);

    mockMvc.perform(get("/study/" + study.getPath() + "/join"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

    Account findAccount = accountRepository.findByNickname("test");
    assertTrue(study.getMembers().contains(findAccount));
  }

  @Test
  @WithAccount("test")
  @DisplayName("스터디 탈퇴")
  void leaveStudy() throws Exception {
    Account subtest = createAccount("subtest");
    Study study = createStudy("test-study", subtest);

    Account findAccount = accountRepository.findByNickname("test");
    studyService.addMember(study, findAccount);

    mockMvc.perform(get("/study/" + study.getPath() + "/leave"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

    assertFalse(study.getMembers().contains(findAccount));
  }

  protected Study createStudy(String path, Account manager) {
    Study study = Study.create().path(path).build();
    studyService.saveStudy(study, manager);
    return study;
  }

  protected Account createAccount(String nickname) {
    Account account = Account.create().nickname(nickname).email(nickname + "@gmail.com").build();
    accountRepository.save(account);
    return account;
  }

}
