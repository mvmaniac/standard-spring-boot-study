package io.devfactory.account.validator;

import io.devfactory.account.domain.Account;
import io.devfactory.account.dto.NicknameFormView;
import io.devfactory.account.repository.AccountRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
public class NicknameFormViewValidator implements Validator {

  private final AccountRepository accountRepository;

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.isAssignableFrom(NicknameFormView.class);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public void validate(Object target, Errors errors) {
    final NicknameFormView nicknameForm = (NicknameFormView) target;

    Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());

    if (Objects.nonNull(byNickname)) {
      errors.rejectValue("nickname", "invalid.value", "입력하신 닉네임을 사용할 수 없습니다.");
    }
  }

}
