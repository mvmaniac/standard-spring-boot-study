package io.devfactory.notification.domain;

import io.devfactory.account.domain.Account;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_notification")
@Entity
public class Notification {

  @Id
  @GeneratedValue
  private Long id;

  private String title;

  private String link;

  private String message;

  private boolean checked;

  @ManyToOne
  private Account account;

  private LocalDateTime createdDateTime;

  @Enumerated(EnumType.STRING)
  private NotificationType notificationType;

  @Builder(builderMethodName = "create")
  private Notification(String title, String link, String message, boolean checked, Account account,
      LocalDateTime createdDateTime, NotificationType notificationType) {
    this.title = title;
    this.link = link;
    this.message = message;
    this.checked = checked;
    this.account = account;
    this.createdDateTime = createdDateTime;
    this.notificationType = notificationType;
  }

  public void changeChecked(boolean checked) {
    this.checked = checked;
  }

}
