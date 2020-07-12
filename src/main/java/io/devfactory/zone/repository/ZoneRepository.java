package io.devfactory.zone.repository;

import io.devfactory.zone.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

  Zone findByCityAndProvince(String city, String Province);

}
