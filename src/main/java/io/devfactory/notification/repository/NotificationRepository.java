package io.devfactory.notification.repository;

import io.devfactory.account.domain.Account;
import io.devfactory.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  long countByAccountAndChecked(Account account, boolean checked);

  List<Notification> findByAccountAndCheckedOrderByCreatedDateTimeDesc(Account account, boolean checked);

  void deleteByAccountAndChecked(Account account, boolean checked);

}
