package io.devfactory.study.domain;

import static javax.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PROTECTED;

import io.devfactory.account.domain.Account;
import io.devfactory.global.config.security.service.UserAccount;
import io.devfactory.tag.domain.Tag;
import io.devfactory.zone.domain.Zone;
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

@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
    @NamedAttributeNode("tags"),
    @NamedAttributeNode("zones"),
    @NamedAttributeNode("managers"),
    @NamedAttributeNode("members")
})
@EqualsAndHashCode(of = "id")
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
  public Study(String path, String title, String shortDescription, String fullDescription,
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

}
