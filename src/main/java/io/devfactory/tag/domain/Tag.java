package io.devfactory.tag.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_tag")
@Entity
public class Tag {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false, unique = true)
  private String title;

  private Tag(String title) {
    this.title = title;
  }

  public static Tag of(String title) {
    return new Tag(title);
  }

}
