package io.devfactory.infra.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Profile("dev")
@Component
public class HtmlEmailService implements EmailService {

  private final JavaMailSender javaMailSender;

  @Override
  public void sendEmail(EmailMessage emailMessage) {
    try {
      final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false,
          String.valueOf(StandardCharsets.UTF_8));

      mimeMessageHelper.setTo(emailMessage.getTo());
      mimeMessageHelper.setSubject(emailMessage.getSubject());
      mimeMessageHelper.setText(emailMessage.getMessage(), true);

      log.info("sent email: {}", emailMessage.getMessage());
      javaMailSender.send(mimeMessage);
    } catch (MessagingException e) {
      log.error("failed to send email", e);
      throw new RuntimeException(e);
    }
  }

}
