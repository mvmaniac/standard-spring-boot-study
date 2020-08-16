package io.devfactory.study.controller;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.devfactory.account.domain.Account;
import io.devfactory.global.config.security.service.CurrentUser;
import io.devfactory.study.domain.Study;
import io.devfactory.study.dto.StudyDescriptionFormView;
import io.devfactory.study.service.StudyService;
import io.devfactory.tag.domain.Tag;
import io.devfactory.tag.dto.TagFormView;
import io.devfactory.tag.repository.TagRepository;
import io.devfactory.tag.service.TagService;
import io.devfactory.zone.domain.Zone;
import io.devfactory.zone.dto.ZoneFormView;
import io.devfactory.zone.repository.ZoneRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
@Controller
public class StudySettingsController {

  private final StudyService studyService;
  private final ModelMapper modelMapper;

  private final TagService tagService;
  private final TagRepository tagRepository;
  private final ZoneRepository zoneRepository;

  private final ObjectMapper objectMapper;

  @GetMapping("/description")
  public String viewStudySetting(@CurrentUser Account account, @PathVariable String path,
      Model model) {

    final Study study = studyService.findStudyToUpdate(account, path);

    model.addAttribute(account);
    model.addAttribute(study);
    model.addAttribute(modelMapper.map(study, StudyDescriptionFormView.class));

    return "views/study/settings/description";
  }

  @PostMapping("/description")
  public String modifyStudyInfo(@CurrentUser Account account, @PathVariable String path,
      @Valid StudyDescriptionFormView studyDescriptionForm, Errors errors,
      Model model, RedirectAttributes attributes) {

    final Study study = studyService.findStudyToUpdate(account, path);

    if (errors.hasErrors()) {
      model.addAttribute(account);
      model.addAttribute(study);
      return "views/study/settings/description";
    }

    studyService.updateStudyDescription(study, studyDescriptionForm);
    attributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");
    return REDIRECT.apply("/study/" + getPath(path) + "/settings/description");
  }

  @GetMapping("/banner")
  public String viewStudyBannerForm(@CurrentUser Account account, @PathVariable String path,
      Model model) {
    final Study study = studyService.findStudyToUpdate(account, path);

    model.addAttribute(account);
    model.addAttribute(study);
    return "views/study/settings/banner";
  }

  @PostMapping("/banner")
  public String modifyStudyBanner(@CurrentUser Account account, @PathVariable String path,
      String image, RedirectAttributes attributes) {
    final Study study = studyService.findStudyToUpdate(account, path);

    studyService.updateStudyBanner(study, image);

    attributes.addFlashAttribute("message", "스터디 이미지를 수정했습니다.");
    return REDIRECT.apply("/study/" + getPath(path) + "/settings/banner");
  }

  @PostMapping("/banner/enable")
  public String enableStudyBanner(@CurrentUser Account account, @PathVariable String path) {
    final Study study = studyService.findStudyToUpdate(account, path);

    studyService.enableStudyBanner(study);
    return REDIRECT.apply("/study/" + getPath(path) + "/settings/banner");
  }

  @PostMapping("/banner/disable")
  public String disableStudyBanner(@CurrentUser Account account, @PathVariable String path) {
    final Study study = studyService.findStudyToUpdate(account, path);

    studyService.disableStudyBanner(study);
    return REDIRECT.apply("/study/" + getPath(path) + "/settings/banner");
  }

  @GetMapping("/tags")
  public String viewTagForm(@CurrentUser Account account, @PathVariable String path, Model model)
      throws JsonProcessingException {
    final Study study = studyService.findStudyToUpdate(account, path);
    model.addAttribute(account);
    model.addAttribute(study);

    model.addAttribute("tags", study.getTags().stream()
        .map(Tag::getTitle).collect(toList()));

    // @formatter:off
    final List<String> allTags = tagRepository.findAll()
        .stream()
        .map(Tag::getTitle)
        .collect(toList())
        ;
    // @formatter:on

    model.addAttribute("whiteList", objectMapper.writeValueAsString(allTags));
    return "views/study/settings/tags";
  }

  @PostMapping("/tags")
  public ResponseEntity<String> createTag(@CurrentUser Account account, @PathVariable String path,
      @RequestBody TagFormView view) {
    final Study study = studyService.findStudyToUpdateTag(account, path);
    final Tag tag = tagService.findOrCreateNew(view.getTagTitle());
    studyService.saveTag(study, tag);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/tags")
  public ResponseEntity<String> removeTag(@CurrentUser Account account, @PathVariable String path,
      @RequestBody TagFormView view) {
    final Study study = studyService.findStudyToUpdateTag(account, path);
    final Tag tag = tagRepository.findByTitle(view.getTagTitle());

    if (Objects.isNull(tag)) {
      return ResponseEntity.badRequest().build();
    }

    studyService.deleteTag(study, tag);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/zones")
  public String viewZoneForm(@CurrentUser Account account, Model model, @PathVariable String path)
      throws JsonProcessingException {
    final Study study = studyService.findStudyToUpdate(account, path);
    model.addAttribute(account);
    model.addAttribute(study);

    model.addAttribute("zones", study.getZones().stream()
        .map(Zone::getZoneName).collect(toList()));

    // @formatter:off
    final List<String> allZones = zoneRepository.findAll()
        .stream()
        .map(Zone::getZoneName)
        .collect(toList())
        ;
    // @formatter:on

    model.addAttribute("whiteList", objectMapper.writeValueAsString(allZones));
    return "views/study/settings/zones";
  }

  @PostMapping("/zones")
  public ResponseEntity<String> createZone(@CurrentUser Account account, @PathVariable String path,
      @RequestBody ZoneFormView view) {
    final Study study = studyService.findStudyToUpdateZone(account, path);
    final Zone zone = zoneRepository
        .findByCityAndProvince(view.getCityName(), view.getProvinceName());

    if (Objects.isNull(zone)) {
      return ResponseEntity.badRequest().build();
    }

    studyService.saveZone(study, zone);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/zones")
  public ResponseEntity<String> removeZone(@CurrentUser Account account, @PathVariable String path,
      @RequestBody ZoneFormView view) {
    final Study study = studyService.findStudyToUpdateZone(account, path);
    final Zone zone = zoneRepository
        .findByCityAndProvince(view.getCityName(), view.getProvinceName());

    if (Objects.isNull(zone)) {
      return ResponseEntity.badRequest().build();
    }

    studyService.deleteZone(study, zone);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/study")
  public String viewStudySettingForm(@CurrentUser Account account, @PathVariable String path, Model model) {
    final Study study = studyService.findStudyToUpdate(account, path);

    model.addAttribute(account);
    model.addAttribute(study);

    return "views/study/settings/study";
  }

  @PostMapping("/study/publish")
  public String publishStudy(@CurrentUser Account account, @PathVariable String path,
      RedirectAttributes attributes) {
    final Study study = studyService.findStudyToUpdateStatus(account, path);
    studyService.publish(study);
    attributes.addFlashAttribute("message", "스터디를 공개했습니다.");
    return REDIRECT.apply("/study/" + getPath(path) + "/settings/study");
  }

  @PostMapping("/study/close")
  public String closeStudy(@CurrentUser Account account, @PathVariable String path,
      RedirectAttributes attributes) {
    final Study study = studyService.findStudyToUpdateStatus(account, path);
    studyService.close(study);

    attributes.addFlashAttribute("message", "스터디를 종료했습니다.");
    return REDIRECT.apply("/study/" + getPath(path) + "/settings/study");
  }

  @PostMapping("/recruit/start")
  public String startRecruit(@CurrentUser Account account, @PathVariable String path, Model model,
      RedirectAttributes attributes) {
    final Study study = studyService.findStudyToUpdateStatus(account, path);

    if (!study.canUpdateRecruiting()) {
      attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
      return REDIRECT.apply("/study/" + getPath(path) + "/settings/study");
    }

    studyService.startRecruit(study);
    attributes.addFlashAttribute("message", "인원 모집을 시작합니다.");
    return REDIRECT.apply("/study/" + getPath(path) + "/settings/study");
  }

  @PostMapping("/recruit/stop")
  public String stopRecruit(@CurrentUser Account account, @PathVariable String path, Model model,
      RedirectAttributes attributes) {
    final Study study = studyService.findStudyToUpdate(account, path);

    if (!study.canUpdateRecruiting()) {
      attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
      return REDIRECT.apply("/study/" + getPath(path) + "/settings/study");
    }

    studyService.stopRecruit(study);
    attributes.addFlashAttribute("message", "인원 모집을 종료합니다.");
    return REDIRECT.apply("/study/" + getPath(path) + "/settings/study");
  }

  private String getPath(String path) {
    return URLEncoder.encode(path, StandardCharsets.UTF_8);
  }

}
