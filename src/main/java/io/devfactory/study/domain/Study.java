package io.devfactory.study.domain;

import static javax.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PROTECTED;

import io.devfactory.account.domain.Account;
import io.devfactory.global.config.security.service.UserAccount;
import io.devfactory.tag.domain.Tag;
import io.devfactory.zone.domain.Zone;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_study")
@Entity
public class Study {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToMany
  private Set<Account> managers = new HashSet<>();

  @ManyToMany
  private Set<Account> members = new HashSet<>();

  @Column(unique = true)
  private String path;

  private String title;

  private String shortDescription;

  @Lob
  @Basic(fetch = EAGER)
  private String fullDescription;

  @Lob
  @Basic(fetch = EAGER)
  private String image;

  @ManyToMany
  private Set<Tag> tags = new HashSet<>();

  @ManyToMany
  private Set<Zone> zones = new HashSet<>();

  private LocalDateTime publishedDateTime;

  private LocalDateTime closedDateTime;

  private LocalDateTime recruitingUpdateDateTime;

  private boolean recruiting;

  private boolean published;

  private boolean closed;

  private boolean useBanner;

  @Builder(builderMethodName = "create")
  private Study(String path, String title, String shortDescription, String fullDescription,
      String image, LocalDateTime publishedDateTime, LocalDateTime closedDateTime,
      LocalDateTime recruitingUpdateDateTime, boolean recruiting, boolean published, boolean closed,
      boolean useBanner) {
    this.path = path;
    this.title = title;
    this.shortDescription = shortDescription;
    this.fullDescription = fullDescription;
    this.image = image;
    this.publishedDateTime = publishedDateTime;
    this.closedDateTime = closedDateTime;
    this.recruitingUpdateDateTime = recruitingUpdateDateTime;
    this.recruiting = recruiting;
    this.published = published;
    this.closed = closed;
    this.useBanner = useBanner;
  }

  public void addManagers(Account account) {
    this.managers.add(account);
  }

  public void addMembers(Account account) {
    this.members.add(account);
  }

  public void removeMember(Account account) {
    this.members.remove(account);
  }

  public boolean isJoinAble(UserAccount userAccount) {
    Account account = userAccount.getAccount();
    return this.isPublished() && this.isRecruiting()
        && !this.members.contains(account) && !this.managers.contains(account);

  }

  public boolean isMember(UserAccount userAccount) {
    return this.members.contains(userAccount.getAccount());
  }

  public boolean isManager(UserAccount userAccount) {
    return this.managers.contains(userAccount.getAccount());
  }

  public void changeImage(String image) {
    this.image = image;
  }

  public void changeUseBanner(boolean isEnable) {
    this.useBanner = isEnable;
  }

  public String getImage() {
    return image != null ? image : "/images/default_banner.png";
  }

  public void publish() {
    if (this.closed) {
      throw new RuntimeException("스터디를 공개할 수 없는 상태입니다. 스터디를 이미 종료했습니다.");
    }
    this.published = !this.published;
    this.publishedDateTime = LocalDateTime.now();
  }

  public void close() {
    if (this.published && !this.closed) {
      this.closed = true;
      this.closedDateTime = LocalDateTime.now();
    } else {
      throw new RuntimeException("스터디를 종료할 수 없습니다. 스터디를 공개하지 않았거나 이미 종료한 스터디입니다.");
    }
  }

  public void startRecruit() {
    if (canUpdateRecruiting()) {
      this.recruiting = true;
      this.recruitingUpdateDateTime = LocalDateTime.now();
    } else {
      throw new RuntimeException("인원 모집을 시작할 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
    }
  }

  public void stopRecruit() {
    if (canUpdateRecruiting()) {
      this.recruiting = false;
      this.recruitingUpdateDateTime = LocalDateTime.now();
    } else {
      throw new RuntimeException("인원 모집을 멈출 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
    }
  }

  public boolean canUpdateRecruiting() {
    return this.published && this.recruitingUpdateDateTime == null || this.recruitingUpdateDateTime
        .isBefore(LocalDateTime.now().minusHours(1));
  }

  public void changePath(String path) {
    this.path = path;
  }

  public void changeTitle(String title) {
    this.title = title;
  }

  public boolean isRemovable() {
    // TODO 모임을 했던 스터디는 삭제할 수 없다.
    return !this.published;
  }

  public String getEncodedPath() {
    return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
  }

  public boolean isManagedBy(Account account) {
    return this.getManagers().contains(account);
  }

}
