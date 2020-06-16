package io.devfactory.account.validator;


import io.devfactory.account.dto.request.SignUpFormRequestView;
import io.devfactory.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class SignUpFormRequestViewValidator implements Validator {

  private final AccountRepository accountRepository;

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.isAssignableFrom(SignUpFormRequestView.class);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public void validate(Object target, Errors errors) {
    final SignUpFormRequestView requestView = (SignUpFormRequestView) target;

    if (accountRepository.existsByEmail(requestView.getEmail())) {
      errors.rejectValue("email", "invalid.email",
          new Object[]{requestView.getEmail()}, "이미 사용 중인 이메일 입니다.");
    }

    if (accountRepository.existsByNickname(requestView.getNickname())) {
      errors.rejectValue("nickname", "invalid.nickname",
          new Object[]{requestView.getNickname()}, "이미 사용 중인 닉네임 입니다.");
    }
  }

}
