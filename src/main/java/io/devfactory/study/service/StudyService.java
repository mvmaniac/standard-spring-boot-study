package io.devfactory.study.service;

import io.devfactory.account.domain.Account;
import io.devfactory.study.domain.Study;
import io.devfactory.study.dto.StudyDescriptionFormView;
import io.devfactory.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StudyService {

  private final StudyRepository studyRepository;
  private final ModelMapper modelMapper;

  @Transactional
  public Study createStudy(Study study, Account account) {
    final Study savedStudy = studyRepository.save(study);
    savedStudy.addManagers(account);
    return savedStudy;
  }

  public Study findStudyByPath(String path) {
    final Study findStudy = studyRepository.findByPath(path);
    if (findStudy == null) {
      throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
    }

    return findStudy;
  }

  public Study findStudyToUpdate(Account account, String path) {
    final Study findStudy = this.findStudyByPath(path);

    if (!account.isManagerOf(findStudy)) {
      throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
    }

    return findStudy;
  }

  @Transactional
  public void updateStudyDescription(Study study, StudyDescriptionFormView view) {
    modelMapper.map(view, study);
  }

}
