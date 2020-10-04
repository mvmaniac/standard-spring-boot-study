package io.devfactory.study.repository;

import io.devfactory.study.domain.Study;
import io.devfactory.tag.domain.Tag;
import io.devfactory.zone.domain.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Set;

public interface StudyRepositoryCustom {

  Page<Study> findByKeyword(String keyword, Pageable pageable);

  List<Study> findByAccount(Set<Tag> tags, Set<Zone> zones);

}
