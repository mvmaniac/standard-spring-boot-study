package io.devfactory.event.service;

import io.devfactory.account.domain.Account;
import io.devfactory.enrollment.domain.Enrollment;
import io.devfactory.enrollment.repository.EnrollmentRepository;
import io.devfactory.event.domain.Event;
import io.devfactory.event.dto.EventFormView;
import io.devfactory.event.repository.EventRepository;
import io.devfactory.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EventService {

  private final EventRepository eventRepository;
  private final EnrollmentRepository enrollmentRepository;

  private final ModelMapper modelMapper;

  @Transactional
  public Event saveEvent(Event event, Study study, Account account) {
    event.changeEvent(study, account);
    return eventRepository.save(event);
  }

  @Transactional
  public void updateEvent(Event findEvent, EventFormView eventFormView) {
    modelMapper.map(eventFormView, findEvent);
    // TODO 모집 인원을 늘린 선착순 모임의 경우에, 자동으로 추가 인원의 참가 신청을 확정 상태로 변경해야 한다. (나중에 할 일)
  }

  @Transactional
  public void deleteEvent(Event event) {
    eventRepository.delete(event);
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
  }

  @Transactional
  public void rejectEnrollment(Event event, Enrollment enrollment) {
    event.reject(enrollment);
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
