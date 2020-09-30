package io.devfactory.event.service;

import io.devfactory.account.domain.Account;
import io.devfactory.enrollment.domain.Enrollment;
import io.devfactory.enrollment.repository.EnrollmentRepository;
import io.devfactory.event.domain.Event;
import io.devfactory.event.dto.EventFormView;
import io.devfactory.event.event.EnrollmentAcceptedEvent;
import io.devfactory.event.event.EnrollmentRejectedEvent;
import io.devfactory.event.repository.EventRepository;
import io.devfactory.study.domain.Study;
import io.devfactory.study.event.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EventService {

  private final EventRepository eventRepository;
  private final EnrollmentRepository enrollmentRepository;

  private final ApplicationEventPublisher eventPublisher;
  private final ModelMapper modelMapper;

  @Transactional
  public Event saveEvent(Event event, Study study, Account account) {
    event.changeEvent(study, account);
    eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
        "'" + event.getTitle() + "' 모임을 만들었습니다."));
    return eventRepository.save(event);
  }

  @Transactional
  public void updateEvent(Event event, EventFormView eventFormView) {
    modelMapper.map(eventFormView, event);
    eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
        "'" + event.getTitle() + "' 모임 정보를 수정했으니 확인하세요."));
  }

  @Transactional
  public void deleteEvent(Event event) {
    eventRepository.delete(event);
//    eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
//        "'" + event.getTitle() + "' 모임을 취소했습니다."));
  }

  @Transactional
  public void newEnrollment(Event event, Account account) {
    if (!enrollmentRepository.existsByEventAndAccount(event, account)) {
      final Enrollment enrollment = Enrollment.create()
          .enrolledAt(LocalDateTime.now())
          .accepted(event.isAbleToAcceptWaitingEnrollment())
          .account(account)
          .build();

      event.addEnrollment(enrollment);
      enrollmentRepository.save(enrollment);
    }
  }

  @Transactional
  public void cancelEnrollment(Event event, Account account) {
    final Enrollment findEnrollment = enrollmentRepository.findByEventAndAccount(event, account);
    event.removeEnrollment(findEnrollment);
    enrollmentRepository.delete(findEnrollment);

    event.acceptNextWaitingEnrollment();
  }

  @Transactional
  public void acceptEnrollment(Event event, Enrollment enrollment) {
    event.accept(enrollment);
    eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));
  }

  @Transactional
  public void rejectEnrollment(Event event, Enrollment enrollment) {
    event.reject(enrollment);
    eventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));
  }

  @Transactional
  public void checkInEnrollment(Enrollment enrollment) {
    enrollment.changeAttended(true);
  }

  @Transactional
  public void cancelCheckInEnrollment(Enrollment enrollment) {
    enrollment.changeAttended(false);
  }

}
