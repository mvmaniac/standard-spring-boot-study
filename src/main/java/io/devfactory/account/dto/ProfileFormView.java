package io.devfactory.account.dto;

import static lombok.AccessLevel.PRIVATE;

import io.devfactory.account.domain.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class ProfileFormView {

  @Length(max = 35)
  private String bio;

  @Length(max = 50)
  private String url;

  @Length(max = 50)
  private String occupation;

  @Length(max = 50)
  private String location;

  private String profileImage;

}
