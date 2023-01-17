package io.devfactory.global.interceptor;

import io.devfactory.global.config.security.service.UserAccount;
import io.devfactory.notification.repository.NotificationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {

  private final NotificationRepository notificationRepository;

  @Override
  public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler,
      ModelAndView modelAndView) throws Exception {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (Objects.nonNull(modelAndView) && !isRedirectView(modelAndView) && Objects.nonNull(authentication) && authentication.getPrincipal() instanceof UserAccount userAccount) {
      final long count = notificationRepository.countByAccountAndChecked(userAccount.getAccount(), false);
      modelAndView.addObject("hasNotification", count > 0);
    }
  }

  private boolean isRedirectView(@NotNull ModelAndView modelAndView) {
    return Objects.requireNonNull(modelAndView.getViewName()).startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
  }

}
