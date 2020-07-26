package io.devfactory.infra.mail;

public interface EmailService {
  void sendEmail(EmailMessage emailMessage);
}
