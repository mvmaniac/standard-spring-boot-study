package io.devfactory.global.common;

import static org.springframework.data.domain.Sort.Direction.ASC;

import io.devfactory.account.domain.Account;
import io.devfactory.account.repository.AccountRepository;
import io.devfactory.enrollment.domain.Enrollment;
import io.devfactory.enrollment.repository.EnrollmentRepository;
import io.devfactory.global.config.security.service.CurrentUser;
import io.devfactory.study.domain.Study;
import io.devfactory.study.repository.StudyRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class MainController {

  private final StudyRepository studyRepository;
  private final AccountRepository accountRepository;
  private final EnrollmentRepository enrollmentRepository;

  @GetMapping("/")
  public String home(@CurrentUser Account account, Model model) {
    if (Objects.nonNull(account)) {
      Account accountLoaded = accountRepository.findAccountWithTagsAndZonesById(account.getId());
      model.addAttribute(accountLoaded);

      model.addAttribute("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(accountLoaded, true));
      model.addAttribute("studyList", studyRepository.findByAccount(accountLoaded.getTags(), accountLoaded.getZones()));
      model.addAttribute("studyManagerOf",
          studyRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
      model.addAttribute("studyMemberOf",
          studyRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
      return "views/index-after-login";
    }

    model.addAttribute("studyList", studyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));
    return "views/index";
  }

  @GetMapping("/login")
  public String viewLogin() {
    return "views/login";
  }

  @GetMapping("/search/study")
  public String search(@PageableDefault(size = 9, sort = "publishedDateTime", direction = ASC)
      Pageable pageable, String keyword, Model model) {
    final Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);

    model.addAttribute("studyPage", studyPage);
    model.addAttribute("keyword", keyword);
    model.addAttribute("sortProperty",
        pageable.getSort().toString().contains("publishedDateTime")
            ? "publishedDateTime" : "memberCount");

    return "views/search";
  }

}
