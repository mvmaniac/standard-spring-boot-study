package io.devfactory.account.validator;

import io.devfactory.account.dto.PasswordFormView;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordFormViewValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.isAssignableFrom(PasswordFormView.class);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public void validate(Object target, Errors errors) {
    final PasswordFormView formView = (PasswordFormView) target;

    if (!formView.getNewPassword().equals(formView.getNewPasswordConfirm())) {
      errors.rejectValue("newPassword", "invalid.password",
          null, "입력한 새 패스워드가 일치하지 않습니다.");
    }
  }

}
