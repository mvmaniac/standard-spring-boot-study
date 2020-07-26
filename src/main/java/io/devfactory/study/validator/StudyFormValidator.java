package io.devfactory.study.validator;

import io.devfactory.study.dto.StudyFormView;
import io.devfactory.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class StudyFormValidator implements Validator {

  private final StudyRepository studyRepository;

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.isAssignableFrom(StudyFormView.class);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public void validate(Object target, Errors errors) {
    final StudyFormView requestView = (StudyFormView) target;

    if (studyRepository.existsByPath(requestView.getPath())) {
      errors.rejectValue("path", "invalid.path","스터디 경로를 사용할 수 없습니다.");
    }
  }

}
