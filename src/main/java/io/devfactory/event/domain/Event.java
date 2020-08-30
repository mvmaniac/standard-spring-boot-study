package io.devfactory.event.domain;

import io.devfactory.account.domain.Account;
import io.devfactory.enrollment.domain.Enrollment;
import io.devfactory.global.config.security.service.UserAccount;
import io.devfactory.study.domain.Study;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_event")
@Entity
public class Event {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name = "study_id")
  private Study study;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account createdBy;

  @Column(nullable = false)
  private String title;

  @Lob
  private String description;

  @Column(nullable = false)
  private LocalDateTime createdDateTime;

  @Column(nullable = false)
  private LocalDateTime endEnrollmentDateTime;

  @Column(nullable = false)
  private LocalDateTime startDateTime;

  @Column(nullable = false)
  private LocalDateTime endDateTime;

  private Integer limitOfEnrollments;

  @OneToMany(mappedBy = "event")
  private List<Enrollment> enrollments = new ArrayList<>();

  @Enumerated(STRING)
  private EventType eventType;

  @Builder(builderMethodName = "create")
  private Event(Study study, Account createdBy, String title, String description,
      LocalDateTime createdDateTime, LocalDateTime endEnrollmentDateTime,
      LocalDateTime startDateTime, LocalDateTime endDateTime, Integer limitOfEnrollments,
      List<Enrollment> enrollments, EventType eventType) {
    this.study = study;
    this.createdBy = createdBy;
    this.title = title;
    this.description = description;
    this.createdDateTime = createdDateTime;
    this.endEnrollmentDateTime = endEnrollmentDateTime;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.limitOfEnrollments = limitOfEnrollments;
    this.enrollments = enrollments;
    this.eventType = eventType;
  }

  public void changeEvent(Study study, Account account) {
    this.study = study;
    this.createdBy = account;
    this.createdDateTime = LocalDateTime.now();
  }

  public boolean isEnrollableFor(UserAccount userAccount) {
    return isNotClosed() && !isAlreadyEnrolled(userAccount);
  }

  public boolean isDisenrollableFor(UserAccount userAccount) {
    return isNotClosed() && isAlreadyEnrolled(userAccount);
  }

  private boolean isNotClosed() {
    return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
  }

  public boolean isAttended(UserAccount userAccount) {
    Account account = userAccount.getAccount();
    for (Enrollment e : this.enrollments) {
      if (e.getAccount().equals(account) && e.isAttended()) {
        return true;
      }
    }
    return false;
  }

  private boolean isAlreadyEnrolled(UserAccount userAccount) {
    Account account = userAccount.getAccount();
    for (Enrollment e : this.enrollments) {
      if (e.getAccount().equals(account)) {
        return true;
      }
    }
    return false;
  }

}
