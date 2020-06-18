package io.devfactory.account.domain;

import static javax.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  @Builder(builderMethodName = "created")
  private Account(String email, String nickname, String password, boolean emailVerified,
      String emailCheckToken, LocalDateTime joinedAt, String bio, String url, String occupation,
      String location, String profileImage, boolean studyCreatedByWeb, boolean studyCreatedByEmail,
      boolean studyEnrollmentResultByWeb, boolean studyEnrollmentResultByEmail,
      boolean studyUpdatedByWeb, boolean studyUpdatedByEmail) {

    this.email = email;
    this.nickname = nickname;
    this.password = password;
    this.emailVerified = emailVerified;
    this.emailCheckToken = emailCheckToken;
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
    return Account.created()
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
  }

  public void completeSingUp() {
    this.emailVerified = true;
    this.joinedAt = LocalDateTime.now();
  }

  public boolean isValidToken(String token) {
    return this.emailCheckToken.equals(token);
  }
}
