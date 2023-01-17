package io.devfactory.study.service;

import io.devfactory.account.domain.Account;
import io.devfactory.study.domain.Study;
import io.devfactory.study.dto.StudyDescriptionFormView;
import io.devfactory.study.event.StudyCreatedEvent;
import io.devfactory.study.event.StudyUpdateEvent;
import io.devfactory.study.repository.StudyRepository;
import io.devfactory.tag.domain.Tag;
import io.devfactory.tag.repository.TagRepository;
import io.devfactory.zone.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static io.devfactory.study.dto.StudyFormView.VALID_PATH_PATTERN;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StudyService {

  private final StudyRepository studyRepository;
  private final TagRepository tagRepository;

  private final ModelMapper modelMapper;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public Study saveStudy(Study study, Account account) {
    final Study savedStudy = studyRepository.save(study);
    savedStudy.addManagers(account);
    return savedStudy;
  }

  public Study findStudyByPath(String path) {
    final Study findStudy = studyRepository.findByPath(path);
    checkIfExistingStudy(path, findStudy);
    return findStudy;
  }

  public Study findStudyToUpdate(Account account, String path) {
    final Study findStudy = this.findStudyByPath(path);
    checkIfManager(account, findStudy);
    return findStudy;
  }

  public Study findStudyToUpdateStatus(Account account, String path) {
    Study study = studyRepository.findStudyWithManagersByPath(path);
    checkIfExistingStudy(path, study);
    checkIfManager(account, study);
    return study;
  }

  @Transactional
  public void updateStudyDescription(Study study, StudyDescriptionFormView view) {
    modelMapper.map(view, study);
    eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디 소개를 수정했습니다."));
  }

  @Transactional
  public void updateStudyBanner(Study study, String image) {
    study.changeImage(image);
  }

  @Transactional
  public void enableStudyBanner(Study study) {
    study.changeUseBanner(true);
  }

  @Transactional
  public void disableStudyBanner(Study study) {
    study.changeUseBanner(false);
  }

  public Study findStudyToUpdateTag(Account account, String path) {
    Study study = studyRepository.findAccountWithTagsByPath(path);
    checkIfExistingStudy(path, study);
    checkIfManager(account, study);
    return study;
  }

  @Transactional
  public void saveTag(Study study, Tag tag) {
    study.getTags().add(tag);
  }

  @Transactional
  public void deleteTag(Study study, Tag tag) {
    study.getTags().remove(tag);
  }

  public Study findStudyToUpdateZone(Account account, String path) {
    Study study = studyRepository.findAccountWithZonesByPath(path);
    checkIfExistingStudy(path, study);
    checkIfManager(account, study);
    return study;
  }

  @Transactional
  public void saveZone(Study study, Zone zone) {
    study.getZones().add(zone);
  }

  @Transactional
  public void deleteZone(Study study, Zone zone) {
    study.getZones().remove(zone);
  }

  @Transactional
  public void publish(Study study) {
    study.publish();

    // 공개인 경우에만 알림을 보낸다
    if (study.isPublished()) {
      eventPublisher.publishEvent(new StudyCreatedEvent(study));
    }
  }

  @Transactional
  public void close(Study study) {
    study.close();
    eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디를 종료했습니다."));
  }

  @Transactional
  public void startRecruit(Study study) {
    study.startRecruit();
    eventPublisher.publishEvent(new StudyUpdateEvent(study, "팀원 모집을 시작합니다."));
  }

  @Transactional
  public void stopRecruit(Study study) {
    study.stopRecruit();
    eventPublisher.publishEvent(new StudyUpdateEvent(study, "팀원 모집을 중단했습니다."));
  }

  public boolean isValidPath(String newPath) {
    if (!newPath.matches(VALID_PATH_PATTERN)) {
      return false;
    }
    return !studyRepository.existsByPath(newPath);
  }

  public boolean isValidTitle(String newTitle) {
    return newTitle.length() <= 50;
  }

  private void checkIfManager(Account account, Study study) {
    if (!study.isManagedBy(account)) {
      throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
    }
  }

  private void checkIfExistingStudy(String path, Study study) {
    if (study == null) {
      throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
    }
  }

  @Transactional
  public void updateStudyPath(Study study, String newPath) {
    study.changePath(newPath);
  }

  @Transactional
  public void updateStudyTitle(Study study, String newTitle) {
    study.changeTitle(newTitle);
  }

  @Transactional
  public void delete(Study study) {
    if (study.isRemovable()) {
      studyRepository.delete(study);
    } else {
      throw new IllegalArgumentException("스터디를 삭제할 수 없습니다.");
    }
  }

  @Transactional
  public void addMember(Study study, Account account) {
    study.addMembers(account);
  }

  @Transactional
  public void removeMember(Study study, Account account) {
    study.removeMember(account);
  }

  public Study findStudyToEnroll(String path) {
    final Study findStudy = studyRepository.findStudyOnlyByPath(path);
    checkIfExistingStudy(path, findStudy);
    return findStudy;
  }

  // TODO: 추후 제거, 테스트용 데이터 생성
  @Transactional
  public void generateTestStudy(Account account) {
    for (int i = 0; i < 30; i++) {
      final String randomValue = RandomString.make(5);
      final Study newStudy = Study.create().title("테스트 스터디" + randomValue)
          .path("test-" + randomValue)
          .shortDescription("테스트용 스터디")
          .fullDescription("test")
          .build();

      newStudy.publish();
      final Study saveStudy = this.saveStudy(newStudy, account);

      final Tag jpa = tagRepository.findByTitle("스프링");
      saveStudy.getTags().add(jpa);
    }
  }

}
