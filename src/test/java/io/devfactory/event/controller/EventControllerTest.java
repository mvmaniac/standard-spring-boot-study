package io.devfactory.event.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.devfactory.account.AccountFactory;
import io.devfactory.account.WithAccount;
import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.enrollment.repository.EnrollmentRepository;
import io.devfactory.event.domain.Event;
import io.devfactory.event.domain.EventType;
import io.devfactory.event.service.EventService;
import io.devfactory.infra.MockMvcTest;
import io.devfactory.study.StudyFactory;
import io.devfactory.study.domain.Study;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@MockMvcTest
class EventControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private StudyFactory studyFactory;

  @Autowired
  private AccountFactory accountFactory;

  @Autowired
  private EventService eventService;

  @Autowired
  private EnrollmentRepository enrollmentRepository;

  @Autowired
  private AccountRepository accountRepository;

  @WithAccount("subtest")
  @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
  @Test
  void newEnrollment_to_FCFS_event_accepted() throws Exception {
    Account test = accountFactory.createAccount("test");
    Study study = studyFactory.createStudy("test-study", test);
    Event event = createEvent("test-event", EventType.FCFS, 2, study, test);

    mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

    Account subtest = accountRepository.findByNickname("subtest");
    isAccepted(subtest, event);
  }

  @WithAccount("subtest")
  @DisplayName("선착순 모임에 참가 신청 - 대기중 (이미 인원이 꽉차서)")
  @Test
  void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
    Account test = accountFactory.createAccount("test");
    Study study = studyFactory.createStudy("test-study", test);
    Event event = createEvent("test-event", EventType.FCFS, 2, study, test);

    Account may = accountFactory.createAccount("may");
    Account june = accountFactory.createAccount("june");
    eventService.newEnrollment(event, may);
    eventService.newEnrollment(event, june);

    mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

    Account subtest = accountRepository.findByNickname("subtest");
    isNotAccepted(subtest, event);
  }

  @WithAccount("subtest")
  @DisplayName("참가신청 확정자가 선착순 모임에 참가 신청을 취소하는 경우, 바로 다음 대기자를 자동으로 신청 확인한다.")
  @Test
  void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
    Account subtest = accountRepository.findByNickname("subtest");
    Account test = accountFactory.createAccount("test");
    Account may = accountFactory.createAccount("may");
    Study study = studyFactory.createStudy("test-study", test);
    Event event = createEvent("test-event", EventType.FCFS, 2, study, test);

    eventService.newEnrollment(event, may);
    eventService.newEnrollment(event, subtest);
    eventService.newEnrollment(event, test);

    isAccepted(may, event);
    isAccepted(subtest, event);
    isNotAccepted(test, event);

    mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

    isAccepted(may, event);
    isAccepted(test, event);
    assertNull(enrollmentRepository.findByEventAndAccount(event, subtest));
  }

  @Test
  @DisplayName("참가신청 비확정자가 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자를 그대로 유지하고 새로운 확정자는 없다.")
  @WithAccount("subtest")
  void not_accepterd_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
    Account subtest = accountRepository.findByNickname("subtest");
    Account test = accountFactory.createAccount("test");
    Account may = accountFactory.createAccount("may");
    Study study = studyFactory.createStudy("test-study", test);
    Event event = createEvent("test-event", EventType.FCFS, 2, study, test);

    eventService.newEnrollment(event, may);
    eventService.newEnrollment(event, test);
    eventService.newEnrollment(event, subtest);

    isAccepted(may, event);
    isAccepted(test, event);
    isNotAccepted(subtest, event);

    mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

    isAccepted(may, event);
    isAccepted(test, event);
    assertNull(enrollmentRepository.findByEventAndAccount(event, subtest));
  }

  private void isNotAccepted(Account account, Event event) {
    assertFalse(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
  }

  private void isAccepted(Account account, Event event) {
    assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
  }

  @Test
  @DisplayName("관리자 확인 모임에 참가 신청 - 대기중")
  @WithAccount("subtest")
  void newEnrollment_to_CONFIMATIVE_event_not_accepted() throws Exception {
    Account test = accountFactory.createAccount("test");
    Study study = studyFactory.createStudy("test-study", test);
    Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, test);

    mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

    Account subtest = accountRepository.findByNickname("subtest");
    isNotAccepted(subtest, event);
  }

  private Event createEvent(String eventTitle, EventType eventType, int limit, Study study,
      Account account) {
    Event event = Event.create()
        .eventType(eventType)
        .limitOfEnrollments(limit)
        .title(eventTitle)
        .createdDateTime(LocalDateTime.now())
        .endEnrollmentDateTime(LocalDateTime.now().plusDays(1))
        .startDateTime(LocalDateTime.now().plusHours(5))
        .endDateTime(LocalDateTime.now().plusHours(7))
        .build();

    return eventService.saveEvent(event, study, account);
  }

}
