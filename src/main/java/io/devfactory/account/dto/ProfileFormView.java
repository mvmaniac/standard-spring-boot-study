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

  @Builder(builderMethodName = "create")
  private ProfileFormView(String bio, String url, String occupation, String location, String profileImage) {
    this.bio = bio;
    this.url = url;
    this.occupation = occupation;
    this.location = location;
    this.profileImage = profileImage;
  }

  public static ProfileFormView of(Account account) {
    return ProfileFormView.create()
          .bio(account.getBio())
          .url(account.getUrl())
          .occupation(account.getOccupation())
          .location(account.getLocation())
          .profileImage(account.getProfileImage())
        .build();
  }

}
