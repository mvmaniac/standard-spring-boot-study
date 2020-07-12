package io.devfactory.zone.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_zone")
@Entity
public class Zone {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String localNameOfCity;

  @Column
  private String province;

  public Zone(String title) {
  }

  @Builder(builderMethodName = "create")
  private Zone(String city, String localNameOfCity, String province) {
    this.city = city;
    this.localNameOfCity = localNameOfCity;
    this.province = province;
  }

  public static Zone of(String city, String localNameOfCity, String province) {
    return new Zone(city, localNameOfCity, province);
  }

  public String getZoneName() {
    return String.format("%s(%s)/%s", this.city, this.localNameOfCity, this.province);
  }

}
