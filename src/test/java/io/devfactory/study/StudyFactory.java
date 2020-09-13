package io.devfactory.study;

import io.devfactory.account.domain.Account;
import io.devfactory.study.domain.Study;
import io.devfactory.study.repository.StudyRepository;
import io.devfactory.study.service.StudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudyFactory {

  @Autowired
  private StudyService studyService;

  @Autowired
  private StudyRepository studyRepository;

  public Study createStudy(String path, Account manager) {
    final Study study = Study.create()
        .path(path)
        .build();

    studyService.saveStudy(study, manager);
    return study;
  }

}
