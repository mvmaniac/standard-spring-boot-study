package io.devfactory.study.repository;

import io.devfactory.account.domain.Account;
import io.devfactory.study.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryCustom {

  boolean existsByPath(String path);

  @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
  Study findByPath(String path);

  @EntityGraph(attributePaths = {"tags", "managers"})
  Study findAccountWithTagsByPath(String path);

  @EntityGraph(attributePaths = {"zones", "managers"})
  Study findAccountWithZonesByPath(String path);

  @EntityGraph(attributePaths = "managers")
  Study findStudyWithManagersByPath(String path);

  @EntityGraph(attributePaths = "members")
  Study findStudyWithMembersByPath(String path);

  Study findStudyOnlyByPath(String path);

  @EntityGraph(attributePaths = {"zones", "tags"})
  Study findStudyWithTagsAndZonesById(Long id);

  @EntityGraph(attributePaths = {"members", "managers"})
  Study findStudyWithManagersAndMembersById(Long id);

  @EntityGraph(attributePaths = {"zones", "tags"})
  List<Study> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

  List<Study> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

  List<Study> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

}
