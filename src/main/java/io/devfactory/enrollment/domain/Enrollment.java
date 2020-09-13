package io.devfactory.enrollment.domain;


import io.devfactory.account.domain.Account;
import io.devfactory.event.domain.Event;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_enrollment")
@Entity
public class Enrollment {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name = "event_id")
  private Event event;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account account;

  private LocalDateTime enrolledAt;

  private boolean accepted;

  private boolean attended;

  @Builder(builderMethodName = "create")
  private Enrollment(Event event, Account account, LocalDateTime enrolledAt, boolean accepted,
      boolean attended) {
    this.event = event;
    this.account = account;
    this.enrolledAt = enrolledAt;
    this.accepted = accepted;
    this.attended = attended;
  }

  public void changeEvent(Event event) {
    this.event = event;
  }

  public void changeAccepted(boolean accepted) {
    this.accepted = accepted;
  }

  public void changeAttended(boolean attended) {
    this.attended = attended;
  }

}
