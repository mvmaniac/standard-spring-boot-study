package io.devfactory.enrollment.domain;


import io.devfactory.account.domain.Account;
import io.devfactory.event.domain.Event;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@NamedEntityGraph(
    name = "Enrollment.withEventAndStudy",
    attributeNodes = {@NamedAttributeNode(value = "event", subgraph = "study")},
    subgraphs = @NamedSubgraph(name = "study", attributeNodes = @NamedAttributeNode("study"))
)
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
