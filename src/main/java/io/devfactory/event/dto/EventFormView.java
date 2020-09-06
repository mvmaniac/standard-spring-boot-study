package io.devfactory.event.dto;

import static lombok.AccessLevel.PRIVATE;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import io.devfactory.event.domain.EventType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import java.time.LocalDateTime;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class EventFormView {

  @NotBlank
  @Length(max = 50)
  private String title;

  private String description;

  private EventType eventType = EventType.FCFS;

  @DateTimeFormat(iso = ISO.DATE_TIME)
  private LocalDateTime endEnrollmentDateTime;

  @DateTimeFormat(iso = ISO.DATE_TIME)
  private LocalDateTime startDateTime;

  @DateTimeFormat(iso = ISO.DATE_TIME)
  private LocalDateTime endDateTime;

  @Min(2)
  private Integer limitOfEnrollments = 2;

  @Builder(builderMethodName = "create")
  public EventFormView(String title, String description, EventType eventType,
      LocalDateTime endEnrollmentDateTime, LocalDateTime startDateTime, LocalDateTime endDateTime,
      Integer limitOfEnrollments) {
    this.title = title;
    this.description = description;
    this.eventType = eventType;
    this.endEnrollmentDateTime = endEnrollmentDateTime;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.limitOfEnrollments = limitOfEnrollments;
  }

  public void changeEventType(EventType eventType) {
    this.eventType = eventType;
  }

}
