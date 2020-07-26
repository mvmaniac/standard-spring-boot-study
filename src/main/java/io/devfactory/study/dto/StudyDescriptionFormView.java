package io.devfactory.study.dto;

import static lombok.AccessLevel.PRIVATE;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class StudyDescriptionFormView {

  @NotBlank
  @Length(max = 100)
  private String shortDescription;

  @NotBlank
  private String fullDescription;

  @Builder(builderMethodName = "create")
  private StudyDescriptionFormView(String shortDescription, String fullDescription) {
    this.shortDescription = shortDescription;
    this.fullDescription = fullDescription;
  }

}
