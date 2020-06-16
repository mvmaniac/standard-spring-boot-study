package io.devfactory.account.controller;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class AccountControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AccountRepository accountRepository;

  @MockBean
  private JavaMailSender javaMailSender;

  @DisplayName("회원 가입 화면 보이는지 테스트")
  @Test
  void viewSignUpForm() throws Exception {
    mockMvc.perform(get("/sign-up"))
        .andExpect(status().isOk())
        .andExpect(view().name("views/account/signUp"))
        .andExpect(model().attributeExists("signUpFormRequestView"))
    ;
  }


  @DisplayName("회원 가입 처리 - 입력값 오류")
  @Test
  void signUpSubmit_with_wrong_input() throws Exception {
    // @formatter:off
    mockMvc.perform(post("/sign-up")
          .param("nickname", "dev")
          .param("email", "email...")
          .param("password", "12345")
          .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("views/account/signUp"))
    ;
    // @formatter:on
  }

  @DisplayName("회원 가입 처리 - 입력값 정상")
  @Test
  void signUpSubmit_with_correct_input() throws Exception {
    // @formatter:off
    mockMvc.perform(post("/sign-up")
          .param("nickname", "dev")
          .param("email", "dev@gmail.com")
          .param("password", "12345678")
          .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/"))
    ;

    final Account findAccount = accountRepository.findByEmail("dev@gmail.com");
    assertNotNull(findAccount);
    assertNotEquals(findAccount.getPassword(), "1234678");

    assertTrue(accountRepository.existsByEmail("dev@gmail.com"));
    then(javaMailSender).should().send(any(SimpleMailMessage.class));
    // @formatter:on
  }

}
