package io.devfactory.study.event;

import io.devfactory.study.domain.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@RequiredArgsConstructor
@Getter
public class StudyUpdateEvent {

  private final Study study;

  private final String message;

}
