package io.devfactory.enrollment.repository;


import io.devfactory.account.domain.Account;
import io.devfactory.enrollment.domain.Enrollment;
import io.devfactory.event.domain.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

  void deleteByEvent(Event event);

  boolean existsByEventAndAccount(Event event, Account account);

  Enrollment findByEventAndAccount(Event event, Account account);

  @EntityGraph("Enrollment.withEventAndStudy")
  List<Enrollment> findByAccountAndAcceptedOrderByEnrolledAtDesc(Account account, boolean accepted);

}
