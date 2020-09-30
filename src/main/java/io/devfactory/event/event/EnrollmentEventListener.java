package io.devfactory.event.event;

import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.enrollment.domain.Enrollment;
import io.devfactory.event.domain.Event;
import io.devfactory.global.config.AppProperties;
import io.devfactory.infra.mail.EmailMessage;
import io.devfactory.infra.mail.EmailService;
import io.devfactory.notification.domain.Notification;
import io.devfactory.notification.domain.NotificationType;
import io.devfactory.notification.repository.NotificationRepository;
import io.devfactory.study.domain.Study;
import io.devfactory.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class EnrollmentEventListener {

  private final NotificationRepository notificationRepository;

  private final EmailService emailService;
  private final TemplateEngine templateEngine;

  private final AppProperties appProperties;

  @EventListener
  public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
    final Enrollment enrollment = enrollmentEvent.getEnrollment();
    final Account account = enrollment.getAccount();
    final Event event = enrollment.getEvent();
    final Study study = event.getStudy();

    if (account.isStudyEnrollmentResultByEmail()) {
      sendEmail(enrollmentEvent, account, event, study);
    }

    if (account.isStudyEnrollmentResultByWeb()) {
      createNotification(enrollmentEvent, account, event, study);
    }
  }

  private void sendEmail(EnrollmentEvent enrollmentEvent, Account account, Event event, Study study) {
    Context context = new Context();
    context.setVariable("nickname", account.getNickname());
    context.setVariable("link", "/study/" + study.getEncodedPath() + "/events/" + event.getId());
    context.setVariable("linkName", study.getTitle());
    context.setVariable("message", enrollmentEvent.getMessage());
    context.setVariable("host", appProperties.getHost());

    final String message = templateEngine.process("views/mail/simple-link", context);

    final EmailMessage emailMessage = EmailMessage.of(account.getEmail(), "스터디올래, " + event.getTitle() + " 모임 참가 신청 결과입니다.", message);

    emailService.sendEmail(emailMessage);
  }

  private void createNotification(EnrollmentEvent enrollmentEvent, Account account, Event event, Study study) {
    Notification notification = Notification.create()
        .title(study.getTitle() + " / " + event.getTitle())
        .link("/study/" + study.getEncodedPath())
        .checked(false)
        .createdDateTime(LocalDateTime.now())
        .message(enrollmentEvent.getMessage())
        .account(account)
        .notificationType(NotificationType.EVENT_ENROLLMENT)
        .build();

    notificationRepository.save(notification);
  }

}
