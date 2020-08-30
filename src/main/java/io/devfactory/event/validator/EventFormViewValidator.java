package io.devfactory.event.validator;

import io.devfactory.event.dto.EventFormView;
import io.devfactory.study.repository.StudyRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EventFormViewValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.isAssignableFrom(EventFormView.class);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public void validate(Object target, Errors errors) {
    final EventFormView requestView = (EventFormView) target;

    if (isNotValidEnrollmentDateTime(requestView)) {
      errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "모임 접수 종료 일시를 정확히 입력하세요.");
    }

    if (isNotValidEndDateTime(requestView)) {
      errors.rejectValue("endDateTime", "wrong.datetime", "모임 종료 일시를 정확히 입력하세요.");
    }

    if (!isNotValidStartDateTime(requestView)) {
      errors.rejectValue("startDateTime", "wrong.datetime", "모임 시작 일시를 정확히 입력하세요.");
    }
  }

  private boolean isNotValidStartDateTime(EventFormView requestView) {
    return requestView.getStartDateTime().isBefore(requestView.getEndEnrollmentDateTime());
  }

  private boolean isNotValidEnrollmentDateTime(EventFormView requestView) {
    return requestView.getEndEnrollmentDateTime().isBefore(LocalDateTime.now());
  }

  private boolean isNotValidEndDateTime(EventFormView requestView) {
    final LocalDateTime endDateTime = requestView.getEndDateTime();
    return endDateTime.isBefore(requestView.getStartDateTime()) || endDateTime
        .isBefore(requestView.getEndEnrollmentDateTime());
  }

}
