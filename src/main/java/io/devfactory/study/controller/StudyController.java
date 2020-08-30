package io.devfactory.study.controller;

import static io.devfactory.global.utils.FunctionUtils.REDIRECT;

import io.devfactory.account.domain.Account;
import io.devfactory.global.config.security.service.CurrentUser;
import io.devfactory.study.domain.Study;
import io.devfactory.study.dto.StudyFormView;
import io.devfactory.study.repository.StudyRepository;
import io.devfactory.study.service.StudyService;
import io.devfactory.study.validator.StudyFormViewValidator;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class StudyController {

  private final StudyService studyService;
  private final StudyRepository studyRepository;

  private final ModelMapper modelMapper;
  private final StudyFormViewValidator studyFormViewValidator;

  @InitBinder("studyFormView")
  public void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(studyFormViewValidator);
  }

  @GetMapping("/study")
  public String viewStudyForm(@CurrentUser Account account, Model model) {
    model.addAttribute("account", account);
    model.addAttribute(StudyFormView.create().build());
    return "views/study/form";
  }

  @GetMapping("study/{path}")
  public String viewStudy(@CurrentUser Account account, @PathVariable("path") String path,
      Model model) {
    final Study findStudy = studyService.findStudyByPath(path);
    model.addAttribute(account);
    model.addAttribute(findStudy);
    return "views/study/view";
  }

  @GetMapping("study/{path}/members")
  public String viewStudyMembers(@CurrentUser Account account, @PathVariable("path") String path,
      Model model) {
    final Study findStudy = studyService.findStudyByPath(path);
    model.addAttribute(account);
    model.addAttribute(findStudy);
    return "views/study/members";
  }

  @PostMapping("/study")
  public String createStudy(@CurrentUser Account account, @Valid StudyFormView view, Errors errors, Model model) {
    if (errors.hasErrors()) {
      model.addAttribute(account);
      return "views/study/form";
    }

    final Study savedStudy = studyService.saveStudy(modelMapper.map(view, Study.class), account);
    return REDIRECT.apply("/study/" + URLEncoder.encode(savedStudy.getPath(),
        StandardCharsets.UTF_8));
  }

  @GetMapping("/study/{path}/join")
  public String joinStudy(@CurrentUser Account account, @PathVariable String path) {
    final Study findStudy = studyRepository.findStudyWithMembersByPath(path);
    studyService.addMember(findStudy, account);
    return REDIRECT.apply("/study/" + findStudy.getEncodedPath() + "/members");
  }

  @GetMapping("/study/{path}/leave")
  public String leaveStudy(@CurrentUser Account account, @PathVariable String path) {
    final Study findStudy = studyRepository.findStudyWithMembersByPath(path);
    studyService.removeMember(findStudy, account);
    return REDIRECT.apply("/study/" + findStudy.getEncodedPath() + "/members");
  }

}
