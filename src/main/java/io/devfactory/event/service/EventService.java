package io.devfactory.event.service;

import io.devfactory.account.domain.Account;
import io.devfactory.event.domain.Event;
import io.devfactory.event.repository.EventRepository;
import io.devfactory.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EventService {

  private final EventRepository eventRepository;

  @Transactional
  public Event saveEvent(Event event, Study study, Account account) {
    event.changeEvent(study, account);
    return eventRepository.save(event);
  }

}
