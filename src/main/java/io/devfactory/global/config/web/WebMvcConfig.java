package io.devfactory.global.config.web;

import io.devfactory.global.interceptor.NotificationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final NotificationInterceptor notificationInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    final List<String> staticResourcePath = Arrays.stream(StaticResourceLocation.values())
        .flatMap(StaticResourceLocation::getPatterns)
        .collect(toList());

    staticResourcePath.add("/node_modules/**");

    registry.addInterceptor(notificationInterceptor)
      .excludePathPatterns(staticResourcePath);
  }

}
