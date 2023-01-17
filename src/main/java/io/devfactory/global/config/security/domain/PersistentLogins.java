package io.devfactory.global.config.security.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
