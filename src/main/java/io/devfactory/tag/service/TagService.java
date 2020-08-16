package io.devfactory.tag.service;

import io.devfactory.tag.domain.Tag;
import io.devfactory.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TagService {

  private final TagRepository tagRepository;

  @Transactional
  public Tag findOrCreateNew(String title) {
    Tag tag = tagRepository.findByTitle(title);
    if (Objects.isNull(tag)) {
      tag = tagRepository.save(Tag.of(title));
    }
    return tag;
  }

}
