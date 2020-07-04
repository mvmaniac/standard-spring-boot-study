package io.devfactory.account.dto;

import static lombok.AccessLevel.PRIVATE;

import io.devfactory.account.domain.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class NotificationFormView {

  private boolean studyCreatedByEmail;

  private boolean studyCreatedByWeb;

  private boolean studyEnrollmentResultByEmail;

  private boolean studyEnrollmentResultByWeb;

  private boolean studyUpdatedByEmail;

  private boolean studyUpdatedByWeb;

}
