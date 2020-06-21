package io.devfactory.global.common;

import io.devfactory.global.config.security.service.CurrentUser;
import io.devfactory.account.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Objects;

@Controller
public class MainController {

  @GetMapping("/")
  public String home(@CurrentUser Account account, Model model) {
    if (Objects.nonNull(account)) {
      model.addAttribute("account", account);
    }
    return "views/index";
  }

}
