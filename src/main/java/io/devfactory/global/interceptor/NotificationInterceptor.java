package io.devfactory.global.interceptor;

import io.devfactory.account.domain.Account;
import io.devfactory.global.config.security.service.UserAccount;
import io.devfactory.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {

  private final NotificationRepository notificationRepository;

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (Objects.nonNull(modelAndView) && !isRedirectView(modelAndView) && Objects.nonNull(authentication) && authentication.getPrincipal() instanceof UserAccount) {
      final Account account = ((UserAccount) authentication.getPrincipal()).getAccount();
      final long count = notificationRepository.countByAccountAndChecked(account, false);
      modelAndView.addObject("hasNotification", count > 0);
    }
  }

  private boolean isRedirectView(@NotNull ModelAndView modelAndView) {
    return modelAndView.getViewName().startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
  }

}
