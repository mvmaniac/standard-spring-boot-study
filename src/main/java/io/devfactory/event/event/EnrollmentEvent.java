package io.devfactory.event.event;

import io.devfactory.enrollment.domain.Enrollment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class EnrollmentEvent {

  protected final Enrollment enrollment;

  protected final String message;

}
