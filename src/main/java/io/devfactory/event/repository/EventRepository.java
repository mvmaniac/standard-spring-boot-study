package io.devfactory.event.repository;

import io.devfactory.event.domain.Event;
import io.devfactory.study.domain.Study;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

  @EntityGraph(value = "Event.withEnrollments", type = EntityGraph.EntityGraphType.LOAD)
  List<Event> findByStudyOrderByStartDateTime(Study study);

}
