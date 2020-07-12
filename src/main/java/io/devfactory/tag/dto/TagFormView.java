package io.devfactory.tag.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class TagFormView {

  private String tagTitle;

  private TagFormView(String tagTitle) {
    this.tagTitle = tagTitle;
  }

  public static TagFormView of(String tagTitle) {
    return new TagFormView(tagTitle);
  }

}
