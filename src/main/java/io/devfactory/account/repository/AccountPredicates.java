package io.devfactory.account.repository;

import com.querydsl.core.types.Predicate;
import io.devfactory.account.domain.QAccount;
import io.devfactory.tag.domain.Tag;
import io.devfactory.zone.domain.Zone;
import java.util.Set;

public class AccountPredicates {

  public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
    final QAccount account = QAccount.account;
    return account.zones.any().in(zones).and(account.tags.any().in(tags));
  }

}
