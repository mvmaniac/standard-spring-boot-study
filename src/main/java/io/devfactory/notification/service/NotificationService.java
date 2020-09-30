package io.devfactory.notification.service;

import io.devfactory.account.domain.Account;
import io.devfactory.notification.domain.Notification;
import io.devfactory.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public long countByAccountAndChecked(Account account, boolean checked) {
    return notificationRepository.countByAccountAndChecked(account, checked);
  }

  @Transactional
  public List<Notification> findByAccountAndCheckedOrderByCreatedDateTimeDesc(Account account, boolean checked) {
    return notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, checked);
  }

  @Transactional
  public void markAsRead(List<Notification> notifications) {
    notifications.forEach(n -> n.changeChecked(true));
    notificationRepository.saveAll(notifications);
  }

  @Transactional
  public void deleteByAccountAndChecked(Account account, boolean checked) {
    notificationRepository.deleteByAccountAndChecked(account, checked);
  }

}
