package io.devfactory.account.dto;

import static lombok.AccessLevel.PRIVATE;

import io.devfactory.account.domain.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class NotificationFormView {

  private boolean studyCreatedByEmail;

  private boolean studyCreatedByWeb;

  private boolean studyEnrollmentResultByEmail;

  private boolean studyEnrollmentResultByWeb;

  private boolean studyUpdatedByEmail;

  private boolean studyUpdatedByWeb;

  @Builder(builderMethodName = "create")
  private NotificationFormView(Account account) {
    this.studyCreatedByEmail = account.isStudyCreatedByEmail();
    this.studyCreatedByWeb = account.isStudyCreatedByWeb();
    this.studyEnrollmentResultByEmail = account.isStudyEnrollmentResultByEmail();
    this.studyEnrollmentResultByWeb = account.isStudyUpdatedByWeb();
    this.studyUpdatedByEmail = account.isStudyUpdatedByEmail();
    this.studyUpdatedByWeb = account.isStudyUpdatedByWeb();
  }

  public static NotificationFormView of(Account account) {
    return new NotificationFormView(account);
  }

}
