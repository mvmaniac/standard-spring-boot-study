package io.devfactory.study.controller;

import io.devfactory.account.domain.Account;
import io.devfactory.global.config.security.service.CurrentUser;
import io.devfactory.study.domain.Study;
import io.devfactory.study.dto.StudyDescriptionFormView;
import io.devfactory.study.service.StudyService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;

@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
@Controller
public class StudySettingsController {

  private final StudyService studyService;
  private final ModelMapper modelMapper;

  @GetMapping("/description")
  public String viewStudySetting(@CurrentUser Account account, @PathVariable String path,
      Model model) {

    Study study = studyService.findStudyToUpdate(account, path);

    model.addAttribute(account);
    model.addAttribute(study);
    model.addAttribute(modelMapper.map(study, StudyDescriptionFormView.class));

    return "views/study/settings/description";
  }

  @PostMapping("/description")
  public String updateStudyInfo(@CurrentUser Account account, @PathVariable String path,
      @Valid StudyDescriptionFormView studyDescriptionForm, Errors errors,
      Model model, RedirectAttributes attributes) {

    Study study = studyService.findStudyToUpdate(account, path);

    if (errors.hasErrors()) {
      model.addAttribute(account);
      model.addAttribute(study);
      return "views/study/settings/description";
    }

    studyService.updateStudyDescription(study, studyDescriptionForm);
    attributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");
    return REDIRECT.apply("/study/" + getPath(path) + "/settings/description");
  }

  private String getPath(String path) {
    return URLEncoder.encode(path, StandardCharsets.UTF_8);
  }

}
