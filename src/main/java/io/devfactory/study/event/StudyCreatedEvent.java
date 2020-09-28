package io.devfactory.study.event;

import io.devfactory.study.domain.Study;
import lombok.Getter;

@Getter
public class StudyCreatedEvent {

  private final Study study;

  public StudyCreatedEvent(Study study) {
    this.study = study;
  }

}
