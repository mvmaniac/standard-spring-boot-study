package io.devfactory.zone.service;

import io.devfactory.zone.domain.Zone;
import io.devfactory.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ZoneService {

  private final ZoneRepository zoneRepository;

  @PostConstruct
  public void initZoneData() throws IOException {
    if (zoneRepository.count() == 0) {
      Resource resource = new ClassPathResource("zones_kr.csv");

      final List<Zone> zoneList = Files
          .readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
          .map(line -> {
            final String[] split = line.split(",");

            return Zone.create()
                .city(split[0])
                .localNameOfCity(split[1])
                .province(split[2])
                .build();
          })
          .toList();

      zoneRepository.saveAll(zoneList);
    }
  }

}
