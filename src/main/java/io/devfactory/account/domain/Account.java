package io.devfactory.account.domain;

import io.devfactory.account.dto.NotificationFormView;
import io.devfactory.account.dto.ProfileFormView;
import io.devfactory.tag.domain.Tag;
import io.devfactory.zone.domain.Zone;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_account")
@Entity
public class Account {

  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private String nickname;

  private String password;

  private boolean emailVerified;

  private String emailCheckToken;

  private LocalDateTime emailCheckTokenGeneratedAt;

  private LocalDateTime joinedAt;

  private String bio;

  private String url;

  private String occupation;

  private String location;

  @Lob
  @Basic(fetch = EAGER)
  private String profileImage;

  private boolean studyCreatedByWeb;

  private boolean studyCreatedByEmail;

  private boolean studyEnrollmentResultByWeb;

  private boolean studyEnrollmentResultByEmail;

  private boolean studyUpdatedByWeb;

  private boolean studyUpdatedByEmail;

  @ManyToMany
  private Set<Tag> tags = new HashSet<>();

  @ManyToMany
  private Set<Zone> zones = new HashSet<>();

  @Builder(builderMethodName = "create")
  private Account(String email, String nickname, String password, boolean emailVerified,
      String emailCheckToken, LocalDateTime emailCheckTokenGeneratedAt, LocalDateTime joinedAt,
      String bio, String url, String occupation, String location, String profileImage,
      boolean studyCreatedByWeb, boolean studyCreatedByEmail, boolean studyEnrollmentResultByWeb,
      boolean studyEnrollmentResultByEmail, boolean studyUpdatedByWeb,
      boolean studyUpdatedByEmail) {

    this.email = email;
    this.nickname = nickname;
    this.password = password;
    this.emailVerified = emailVerified;
    this.emailCheckToken = emailCheckToken;
    this.emailCheckTokenGeneratedAt = emailCheckTokenGeneratedAt;
    this.joinedAt = joinedAt;
    this.bio = bio;
    this.url = url;
    this.occupation = occupation;
    this.location = location;
    this.profileImage = profileImage;
    this.studyCreatedByWeb = studyCreatedByWeb;
    this.studyCreatedByEmail = studyCreatedByEmail;
    this.studyEnrollmentResultByWeb = studyEnrollmentResultByWeb;
    this.studyEnrollmentResultByEmail = studyEnrollmentResultByEmail;
    this.studyUpdatedByWeb = studyUpdatedByWeb;
    this.studyUpdatedByEmail = studyUpdatedByEmail;
  }

  public static Account of(String email, String nickname, String password) {
    return Account.create()
        .email(email)
        .nickname(nickname)
        .password(password)
        .studyCreatedByWeb(true)
        .studyEnrollmentResultByWeb(true)
        .studyUpdatedByWeb(true)
        .build();
  }

  public void generateEmailCheckToken() {
    this.emailCheckToken = UUID.randomUUID().toString();
    this.emailCheckTokenGeneratedAt = LocalDateTime.now();
  }

  public void completeSingUp() {
    this.emailVerified = true;
    this.joinedAt = LocalDateTime.now();
  }

  public boolean isValidToken(String token) {
    return this.emailCheckToken.equals(token);
  }

  public boolean canSendConfirmEmail() {
    return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
  }

  public void updateProfile(ProfileFormView view) {
    this.bio = view.getBio();
    this.url = view.getUrl();
    this.occupation = view.getOccupation();
    this.location = view.getLocation();
    this.profileImage = view.getProfileImage();
  }

  public void updatePassword(String newPassword) {
    this.password = newPassword;
  }

  public void updateNotification(NotificationFormView view) {
    this.studyCreatedByWeb = view.isStudyCreatedByWeb();
    this.studyCreatedByEmail = view.isStudyCreatedByEmail();
    this.studyUpdatedByWeb = view.isStudyUpdatedByWeb();
    this.studyUpdatedByEmail = view.isStudyUpdatedByEmail();
    this.studyEnrollmentResultByEmail =view.isStudyEnrollmentResultByEmail();
    this.studyEnrollmentResultByWeb = view.isStudyEnrollmentResultByWeb();
  }

  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }

}
