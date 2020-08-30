package io.devfactory.event.controller;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;

import io.devfactory.account.domain.Account;
import io.devfactory.event.domain.Event;
import io.devfactory.event.dto.EventFormView;
import io.devfactory.event.repository.EventRepository;
import io.devfactory.event.service.EventService;
import io.devfactory.event.validator.EventFormViewValidator;
import io.devfactory.global.config.security.service.CurrentUser;
import io.devfactory.study.domain.Study;
import io.devfactory.study.service.StudyService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
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

  @GetMapping("/event")
  public String viewEventForm(@CurrentUser Account account, @PathVariable String path,
      Model model) {
    final Study findStudy = studyService.findStudyToUpdateStatus(account, path);
    model.addAttribute(findStudy);
    model.addAttribute(account);
    model.addAttribute(EventFormView.create().build());
    return "views/event/form";
  }

  @PostMapping("/event")
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
        .apply("views/study" + findStudy.getEncodedPath() + "/events/" + saveEvent.getId());
  }

  @GetMapping("/events/{id}")
  public String getEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
      Model model) {
    model.addAttribute(account);
    model.addAttribute(eventRepository.findById(id).orElseThrow());
    model.addAttribute(studyService.findStudyByPath(path));
    return "views/event/view";
  }

}
