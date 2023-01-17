package io.devfactory.event.controller;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;

import io.devfactory.account.domain.Account;
import io.devfactory.enrollment.domain.Enrollment;
import io.devfactory.event.domain.Event;
import io.devfactory.event.dto.EventFormView;
import io.devfactory.event.repository.EventRepository;
import io.devfactory.event.service.EventService;
import io.devfactory.event.validator.EventFormViewValidator;
import io.devfactory.global.config.security.service.CurrentUser;
import io.devfactory.study.domain.Study;
import io.devfactory.study.service.StudyService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/study/{path}")
@Controller
public class EventController {

  private final StudyService studyService;
  private final EventService eventService;

  private final EventRepository eventRepository;

  private final ModelMapper modelMapper;
  private final EventFormViewValidator eventFormViewValidator;

  @InitBinder("eventFormView")
  public void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(eventFormViewValidator);
  }

  @GetMapping("/events/form")
  public String viewEventForm(@CurrentUser Account account, @PathVariable String path,
      Model model) {
    final Study findStudy = studyService.findStudyToUpdateStatus(account, path);
    model.addAttribute(findStudy);
    model.addAttribute(account);
    model.addAttribute(EventFormView.create().build());
    return "views/event/form";
  }

  @PostMapping("/events")
  public String createEvent(@CurrentUser Account account, @PathVariable String path,
      @Valid EventFormView view, Errors errors, Model model) {

    final Study findStudy = studyService.findStudyToUpdateStatus(account, path);

    if (errors.hasErrors()) {
      model.addAttribute(account);
      model.addAttribute(findStudy);
      return "views/event/form";
    }

    final Event saveEvent = eventService
        .saveEvent(modelMapper.map(view, Event.class), findStudy, account);

    return REDIRECT
        .apply("/study/" + findStudy.getEncodedPath() + "/events/" + saveEvent.getId());
  }

  @GetMapping("/events/{id}/form-modify")
  public String modifyEventForm(@CurrentUser Account account,
      @PathVariable String path, @PathVariable Long id, Model model) {

    final Study findStudy = studyService.findStudyToUpdate(account, path);
    final Event findEvent = eventRepository.findById(id).orElseThrow();

    model.addAttribute(findStudy);
    model.addAttribute(account);
    model.addAttribute(findEvent);
    model.addAttribute(modelMapper.map(findEvent, EventFormView.class));

    return "views/event/form-modify";
  }

  @PostMapping("/events/{id}")
  public String modifyEvent(@CurrentUser Account account, @PathVariable String path,
      @PathVariable Long id, @Valid EventFormView eventFormView, Errors errors,
      Model model) {
    final Study findStudy = studyService.findStudyToUpdate(account, path);
    final Event findEvent = eventRepository.findById(id).orElseThrow();

    eventFormView.changeEventType(findEvent.getEventType());

    eventFormViewValidator.validateUpdateForm(eventFormView, findEvent, errors);

    if (errors.hasErrors()) {
      model.addAttribute(account);
      model.addAttribute(findStudy);
      model.addAttribute(findEvent);

      return "views/event/form-modify";
    }

    eventService.updateEvent(findEvent, eventFormView);
    return REDIRECT
        .apply("/study/" + findStudy.getEncodedPath() + "/events/" + findEvent.getId());
  }

  @GetMapping("/events/{id}")
  public String retrieveEvent(@CurrentUser Account account, @PathVariable String path,
      @PathVariable Long id,
      Model model) {
    model.addAttribute(account);
    model.addAttribute(eventRepository.findById(id).orElseThrow());
    model.addAttribute(studyService.findStudyByPath(path));
    return "views/event/view";
  }

  @GetMapping("/events")
  public String retrieveEvents(@CurrentUser Account account, @PathVariable String path,
      Model model) {
    Study study = studyService.findStudyByPath(path);
    model.addAttribute(account);
    model.addAttribute(study);

    List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);
    List<Event> newEvents = new ArrayList<>();
    List<Event> oldEvents = new ArrayList<>();

    events.forEach(e -> {
      if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
        oldEvents.add(e);
      } else {
        newEvents.add(e);
      }
    });

    model.addAttribute("newEvents", newEvents);
    model.addAttribute("oldEvents", oldEvents);

    return "views/study/event";
  }

  @DeleteMapping("/events/{id}")
  public String removeEvent(@CurrentUser Account account, @PathVariable String path,
      @PathVariable Long id) {
    final Study findStudy = studyService.findStudyToUpdate(account, path);
    eventService.deleteEvent(eventRepository.findById(id).orElseThrow());
    return REDIRECT.apply("/study/" + findStudy.getEncodedPath() + "/events");
  }

  @PostMapping("/events/{id}/enroll")
  public String newEnrollment(@CurrentUser Account account, @PathVariable String path,
      @PathVariable("id") Event event) {
    final Study findStudy = studyService.findStudyToEnroll(path);
    eventService.newEnrollment(event, account);
    return REDIRECT.apply("/study/" + findStudy.getEncodedPath() + "/events/" + event.getId());
  }

  @PostMapping("/events/{id}/disenroll")
  public String cancelEnrollment(@CurrentUser Account account, @PathVariable String path,
      @PathVariable("id") Event event) {
    final Study findStudy = studyService.findStudyToEnroll(path);
    eventService.cancelEnrollment(event, account);
    return REDIRECT.apply("/study/" + findStudy.getEncodedPath() + "/events/" + event.getId());
  }

  @GetMapping("events/{eventId}/enrollments/{enrollmentId}/accept")
  public String acceptEnrollment(@CurrentUser Account account, @PathVariable String path,
      @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
    final Study findStudy = studyService.findStudyToUpdate(account, path);
    eventService.acceptEnrollment(event, enrollment);
    return REDIRECT.apply("/study/" + findStudy.getEncodedPath() + "/events/" + event.getId());
  }

  @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
  public String rejectEnrollment(@CurrentUser Account account, @PathVariable String path,
      @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
    final Study findStudy = studyService.findStudyToUpdate(account, path);
    eventService.rejectEnrollment(event, enrollment);
    return REDIRECT.apply("/study/" + findStudy.getEncodedPath() + "/events/" + event.getId());
  }

  @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/checkin")
  public String checkInEnrollment(@CurrentUser Account account, @PathVariable String path,
      @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
    final Study findStudy = studyService.findStudyToUpdate(account, path);
    eventService.checkInEnrollment(enrollment);
    return REDIRECT.apply("/study/" + findStudy.getEncodedPath() + "/events/" + event.getId());
  }

  @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin")
  public String cancelCheckInEnrollment(@CurrentUser Account account, @PathVariable String path,
      @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
    final Study findStudy = studyService.findStudyToUpdate(account, path);
    eventService.cancelCheckInEnrollment(enrollment);
    return REDIRECT.apply("/study/" + findStudy.getEncodedPath() + "/events/" + event.getId());
  }

}
