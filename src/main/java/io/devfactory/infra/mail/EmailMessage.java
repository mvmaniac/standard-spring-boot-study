package io.devfactory.infra.mail;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class EmailMessage {

  private String to;

  private String subject;

  private String message;

  @Builder(builderMethodName = "create")
  private EmailMessage(String to, String subject, String message) {
    this.to = to;
    this.subject = subject;
    this.message = message;
  }

  public static EmailMessage of(String to, String subject, String message) {
    return EmailMessage.create().to(to).subject(subject).message(message).build();
  }

}
