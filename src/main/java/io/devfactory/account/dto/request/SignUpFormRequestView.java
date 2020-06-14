package io.devfactory.account.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class SignUpFormRequestView {

  @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
  @Length(min = 3, max = 20)
  @NotBlank
  private String nickname;

  @Email
  @NotBlank
  private String email;

  @Length(min = 8, max = 50)
  @NotBlank
  private String password;

  @Builder(builderMethodName = "create")
  public SignUpFormRequestView(String nickname, String email, String password) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;
  }

}
