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
        final Context context = new Context();
        context.setVariable("link", "/study/" + findStudy.getEncodedPath());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", findStudy.getTitle());
        context.setVariable("message", "새로운 스터디가 생겼습니다.");
        context.setVariable("host", appProperties.getHost());

        final String message = templateEngine.process("views/mail/simple-link", context);

        final EmailMessage emailMessage = EmailMessage
            .of(account.getEmail(), "스터디 올래 '" + findStudy.getTitle() + "' 스터디가 생겼습니다.", message);

        emailService.sendEmail(emailMessage);
      }

      if (account.isStudyCreatedByWeb()) {
        Notification notification = Notification.create()
            .title(findStudy.getTitle())
            .link("/study/" + findStudy.getEncodedPath())
            .checked(false)
            .createdLocalDataTime(LocalDateTime.now())
            .message(findStudy.getShortDescription())
            .account(account)
            .notificationType(NotificationType.STUDY_CREATED)
            .build();

        notificationRepository.save(notification);
      }
    });

    log.debug("[dev] {} is created.", findStudy.getTitle());
  }

}
