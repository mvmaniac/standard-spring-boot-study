package io.devfactory.account.controller;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.infra.AbstractContainerBaseTest;
import io.devfactory.infra.MockMvcTest;
import io.devfactory.infra.mail.EmailMessage;
import io.devfactory.infra.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@MockMvcTest
class AccountControllerTest extends AbstractContainerBaseTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AccountRepository accountRepository;

  @MockBean
  private EmailService emailService;

  @DisplayName("회원 가입 화면 보이는지 테스트")
  @Test
  void viewSignUpForm() throws Exception {
    mockMvc
        .perform(get("/sign-up"))
        .andExpect(status().isOk())
        .andExpect(view().name("views/account/signUp"))
        .andExpect(model().attributeExists("signUpFormRequestView"))
    ;
  }

  @DisplayName("회원 가입 처리 - 입력값 오류")
  @Test
  void signUpSubmit_with_wrong_input() throws Exception {
    mockMvc
        .perform(post("/sign-up")
            .param("nickname", "dev")
            .param("email", "email...")
            .param("password", "12345")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("views/account/signUp"))
        .andExpect(unauthenticated())
    ;
  }

  @DisplayName("회원 가입 처리 - 입력값 정상")
  @Test
  void signUpSubmit_with_correct_input() throws Exception {
    final String email = "dev1@gmail.com";
    final String password = "12345678";

    mockMvc
        .perform(post("/sign-up")
            .param("nickname", "dev1")
            .param("email", email)
            .param("password", password)
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/"))
        .andExpect(authenticated().withUsername("dev1"))
    ;

    final Account findAccount = accountRepository.findByEmail(email);
    assertNotNull(findAccount);
    assertNotNull(findAccount.getEmailCheckToken());
    assertNotEquals(findAccount.getPassword(), password);

    assertTrue(accountRepository.existsByEmail(email));
    then(emailService).should().sendEmail(any(EmailMessage.class));
  }

  @DisplayName("인증 메일 확인 - 입력 값 오류")
  @Test
  void checkEmailToke_with_wrong_input() throws Exception {
    mockMvc
        .perform(get("/check-email-token")
            .param("token", "wrongToken")
            .param("email", "dev@gmail.com"))
        .andExpect(status().isOk())
        .andExpect(view().name("views/account/checkedEmail"))
        .andExpect(model().attributeExists("error"))
        .andExpect(unauthenticated())
    ;
  }

  @Transactional
  @DisplayName("인증 메일 확인 - 입력 값 정상")
  @Test
  void checkEmailToke_with_correct_input() throws Exception {
    final Account account = Account.of("dev2@gmail.com", "dev2", "1234");

    final Account savedAccount = accountRepository.save(account);
    savedAccount.generateEmailCheckToken();

    mockMvc
        .perform(get("/check-email-token")
            .param("token", savedAccount.getEmailCheckToken())
            .param("email", savedAccount.getEmail()))
        .andExpect(status().isOk())
        .andExpect(view().name("views/account/checkedEmail"))
        .andExpect(model().attributeDoesNotExist("error"))
        .andExpect(model().attributeExists("nickname"))
        .andExpect(model().attributeExists("numberOfUser"))
        .andExpect(authenticated().withUsername("dev2"))
    ;
  }

}
