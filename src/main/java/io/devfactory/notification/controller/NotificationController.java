package io.devfactory.notification.controller;

import io.devfactory.account.domain.Account;
import io.devfactory.global.config.security.service.CurrentUser;
import io.devfactory.notification.domain.Notification;
import io.devfactory.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.List;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;

@RequiredArgsConstructor
@Controller
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping("/notifications")
  public String getNotifications(@CurrentUser Account account, Model model) {
    List<Notification> notifications = notificationService.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, false);
    long numberOfChecked = notificationService.countByAccountAndChecked(account, true);
    putCategorizedNotifications(model, notifications, numberOfChecked, notifications.size());
    model.addAttribute("isNew", true);
    notificationService.markAsRead(notifications);
    return "views/notification/list";
  }

  @GetMapping("/notifications/old")
  public String getOldNotifications(@CurrentUser Account account, Model model) {
    List<Notification> notifications = notificationService.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, true);
    long numberOfNotChecked = notificationService.countByAccountAndChecked(account, false);
    putCategorizedNotifications(model, notifications, notifications.size(), numberOfNotChecked);
    model.addAttribute("isNew", false);
    return "views/notification/list";
  }

  @DeleteMapping("/notifications")
  public String deleteNotifications(@CurrentUser Account account) {
    notificationService.deleteByAccountAndChecked(account, true);
    return REDIRECT.apply("/notifications");
  }

  private void putCategorizedNotifications(Model model, List<Notification> notifications,
      long numberOfChecked, long numberOfNotChecked) {

    List<Notification> newStudyNotifications = new ArrayList<>();
    List<Notification> eventEnrollmentNotifications = new ArrayList<>();
    List<Notification> watchingStudyNotifications = new ArrayList<>();

    for (var notification : notifications) {
      switch (notification.getNotificationType()) {
        case STUDY_CREATED: newStudyNotifications.add(notification); break;
        case EVENT_ENROLLMENT: eventEnrollmentNotifications.add(notification); break;
        case STUDY_UPDATED: watchingStudyNotifications.add(notification); break;
      }
    }

    model.addAttribute("numberOfNotChecked", numberOfNotChecked);
    model.addAttribute("numberOfChecked", numberOfChecked);
    model.addAttribute("notifications", notifications);
    model.addAttribute("newStudyNotifications", newStudyNotifications);
    model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
    model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);
  }

}
