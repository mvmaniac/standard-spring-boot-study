package io.devfactory.zone.repository;

import io.devfactory.zone.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ZoneRepository extends JpaRepository<Zone, Long> {

  Zone findByCityAndProvince(String city, String Province);

}
