package io.devfactory.account.dto;

import static lombok.AccessLevel.PRIVATE;

import io.devfactory.account.domain.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class PasswordFormView {

  @Length(min = 8, max = 50)
  private String newPassword;

  @Length(min = 8, max = 50)
  private String newPasswordConfirm;

  @Builder(builderMethodName = "create")
  private PasswordFormView(String newPassword, String newPasswordConfirm) {
    this.newPassword = newPassword;
    this.newPasswordConfirm = newPasswordConfirm;
  }

  public static PasswordFormView of() {
    return new PasswordFormView();
  }

}
