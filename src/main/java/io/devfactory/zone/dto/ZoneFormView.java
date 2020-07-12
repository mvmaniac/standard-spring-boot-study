package io.devfactory.zone.dto;

import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class ZoneFormView {

  private String zoneName;

  private ZoneFormView(String zoneName) {
    this.zoneName = zoneName;
  }

  public static ZoneFormView of(String zoneName) {
    return new ZoneFormView(zoneName);
  }

  public String getCityName() {
    return zoneName.substring(0, zoneName.indexOf("("));
  }

  public String getProvinceName() {
    return zoneName.substring(zoneName.indexOf("/") + 1);
  }

  public String getLocalNameOfCity() {
    return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
  }

}
