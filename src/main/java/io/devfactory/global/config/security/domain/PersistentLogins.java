package io.devfactory.global.config.security.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "persistent_logins")
@Entity
public class PersistentLogins {

  @Id
  @Column(length = 64)
  private String series;

  @Column(nullable = false, length = 64)
  private String username;

  @Column(nullable = false, length = 64)
  private String token;

  @Column(nullable = false, length = 64)
  private LocalDateTime lastUsed;

}
