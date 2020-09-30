package io.devfactory.study.event;

import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountPredicates;
import io.devfactory.account.repository.AccountRepository;
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
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class StudyEventListener {

  private final StudyRepository studyRepository;
  private final AccountRepository accountRepository;
  private final NotificationRepository notificationRepository;

  private final EmailService emailService;
  private final TemplateEngine templateEngine;

  private final AppProperties appProperties;

  @Transactional
  @EventListener
  public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
    final Study findStudy = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());

    final Iterable<Account> accounts = accountRepository
        .findAll(AccountPredicates.findByTagsAndZones(findStudy.getTags(), findStudy.getZones()));

    accounts.forEach(account -> {
      if (account.isStudyCreatedByEmail()) {
        sendStudyCreatedEmail(findStudy, account, "새로운 스터디가 생겼습니다",
            "스터디올래, '" + findStudy.getTitle() + "' 스터디가 생겼습니다.");
      }

      if (account.isStudyCreatedByWeb()) {
        createNotification(findStudy, account, findStudy.getShortDescription(), NotificationType.STUDY_CREATED);
      }
    });

    log.debug("[dev] {} is created.", findStudy.getTitle());
  }

  @EventListener
  public void handleStudyUpdateEvent(StudyUpdateEvent studyUpdateEvent) {
    final Study findStudy = studyRepository.findStudyWithManagersAndMembersById(studyUpdateEvent.getStudy().getId());

    Set<Account> accounts = new HashSet<>();
    accounts.addAll(findStudy.getManagers());
    accounts.addAll(findStudy.getMembers());

    accounts.forEach(account -> {
      if (account.isStudyUpdatedByEmail()) {
        sendStudyCreatedEmail(findStudy, account, studyUpdateEvent.getMessage(),
            "스터디올래, '" + findStudy.getTitle() + "' 스터디에 새소식이 있습니다.");
      }

      if (account.isStudyUpdatedByWeb()) {
        createNotification(findStudy, account, studyUpdateEvent.getMessage(), NotificationType.STUDY_UPDATED);
      }
    });
  }

  private void sendStudyCreatedEmail(Study study, Account account, String contextMessage, String emailSubject) {
    Context context = new Context();
    context.setVariable("nickname", account.getNickname());
    context.setVariable("link", "/study/" + study.getEncodedPath());
    context.setVariable("linkName", study.getTitle());
    context.setVariable("message", contextMessage);
    context.setVariable("host", appProperties.getHost());

    final String message = templateEngine.process("views/mail/simple-link", context);

    final EmailMessage emailMessage = EmailMessage.of(account.getEmail(), emailSubject, message);

    emailService.sendEmail(emailMessage);
  }

  private void createNotification(Study study, Account account, String message, NotificationType notificationType) {
    Notification notification = Notification.create()
        .title(study.getTitle())
        .link("/study/" + study.getEncodedPath())
        .checked(false)
        .createdDateTime(LocalDateTime.now())
        .message(message)
        .account(account)
        .notificationType(notificationType)
        .build();

    notificationRepository.save(notification);
  }

}
