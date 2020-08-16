package io.devfactory.study.repository;

import io.devfactory.study.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {

  boolean existsByPath(String path);

  @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
  Study findByPath(String path);

  @EntityGraph(value = "Study.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
  Study findAccountWithTagsByPath(String path);

  @EntityGraph(value = "Study.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
  Study findAccountWithZonesByPath(String path);

  @EntityGraph(value = "Study.withManagers", type = EntityGraph.EntityGraphType.FETCH)
  Study findStudyWithManagersByPath(String path);

}
