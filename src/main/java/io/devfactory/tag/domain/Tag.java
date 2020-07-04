package io.devfactory.tag.domain;

import static lombok.AccessLevel.PROTECTED;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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
