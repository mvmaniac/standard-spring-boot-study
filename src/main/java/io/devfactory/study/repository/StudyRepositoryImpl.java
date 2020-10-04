package io.devfactory.study.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import io.devfactory.study.domain.QStudy;
import io.devfactory.study.domain.Study;
import io.devfactory.tag.domain.QTag;
import io.devfactory.tag.domain.Tag;
import io.devfactory.zone.domain.QZone;
import io.devfactory.zone.domain.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class StudyRepositoryImpl extends QuerydslRepositorySupport implements
    StudyRepositoryCustom {

  public StudyRepositoryImpl() {
    super(Study.class);
  }

  @Override
  public Page<Study> findByKeyword(String keyword, Pageable pageable) {
    final QStudy study = QStudy.study;

    final JPQLQuery<Study> query = from(study)
        .where(study.published.isTrue()
            .and(study.title.containsIgnoreCase(keyword))
            .or(study.tags.any().title.containsIgnoreCase(keyword))
            .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
        .leftJoin(study.tags, QTag.tag).fetchJoin()
        .leftJoin(study.zones, QZone.zone).fetchJoin()
        .distinct();

    final JPQLQuery<Study> pageableQuery = getQuerydsl().applyPagination(pageable, query);
    final QueryResults<Study> fetchResults = pageableQuery.fetchResults();

    return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
  }

  @Override
  public List<Study> findByAccount(Set<Tag> tags, Set<Zone> zones) {
    final QStudy study = QStudy.study;

    final JPQLQuery<Study> query = from(study)
        .where(study.published.isTrue()
          .and(study.closed.isFalse())
          .and(study.tags.any().in(tags))
          .and(study.zones.any().in(zones)))
        .leftJoin(study.tags, QTag.tag).fetchJoin()
        .leftJoin(study.zones, QZone.zone).fetchJoin()
        .orderBy(study.publishedDateTime.desc())
        .distinct()
        .limit(9);

    return query.fetch();
  }

}
