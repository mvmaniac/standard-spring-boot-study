package io.devfactory.tag.repository;

import io.devfactory.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

  Tag findByTitle(String title);

}