package io.devfactory.study.dto;

import static lombok.AccessLevel.PRIVATE;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class StudyFormView {

  @NotBlank
  @Length(min = 3, max = 20)
  @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
  private String path;

  @NotBlank
  @Length(max = 50)
  private String title;

  @NotBlank
  @Length(max = 100)
  private String shortDescription;

  @NotBlank
  private String fullDescription;

  @Builder(builderMethodName = "create")
  private StudyFormView(String path, String title, String shortDescription, String fullDescription) {
    this.path = path;
    this.title = title;
    this.shortDescription = shortDescription;
    this.fullDescription = fullDescription;
  }

}
