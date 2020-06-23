package io.devfactory.global.common;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.devfactory.account.dto.request.SignUpFormRequestView;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

@RequiredArgsConstructor
@TestConstructor(autowireMode = ALL)
@AutoConfigureMockMvc
@SpringBootTest
class MainControllerTest {

  private final MockMvc mockMvc;
  private final AccountService accountService;
  private final AccountRepository accountRepository;

  @BeforeEach
  void beforeEach() {
    final SignUpFormRequestView signUpFormRequestView = SignUpFormRequestView.create()
        .email("dev@gmail.com")
        .nickname("dev")
        .password("12345678")
        .build();
    accountService.processSaveAccount(signUpFormRequestView);
  }

  @AfterEach
  void afterEach() {
    accountRepository.deleteAll();
  }

  @DisplayName("이메일로 로그인 성공")
  @Test
  void login_with_email() throws Exception {
    mockMvc
        .perform(post("/login")
            .param("username", "dev@gmail.com")
            .param("password", "12345678")
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"))
        .andExpect(authenticated().withUsername("dev"))
    ;
  }

  @DisplayName("닉네임으로 로그인 성공")
  @Test
  void login_with_nickname() throws Exception {
    mockMvc
        .perform(post("/login")
            .param("username", "dev")
            .param("password", "12345678")
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"))
        .andExpect(authenticated().withUsername("dev"))
    ;
  }

  @DisplayName("로그인 실패")
  @Test
  void login_fail() throws Exception {
    mockMvc
        .perform(post("/login")
            .param("username", "dev1")
            .param("password", "12345678")
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login?error"))
        .andExpect(unauthenticated())
    ;
  }

  @DisplayName("로그아웃")
  @Test
  void logout() throws Exception {
    mockMvc
        .perform(post("/logout")
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"))
        .andExpect(unauthenticated())
    ;
  }

}
