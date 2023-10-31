package com.chat.yourway.repository.impl;

import com.chat.yourway.repository.OnlineContactRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OnlineContactRepositoryImpl implements OnlineContactRepository {
  private static final Set<String> onlineContactEmails = ConcurrentHashMap.newKeySet();

  @Override
  public void save(String email) {
    onlineContactEmails.add(email);
  }

  @Override
  public void delete(String email) {
    onlineContactEmails.remove(email);
  }

  @Override
  public boolean contains(String email) {
    return onlineContactEmails.contains(email);
  }
}
