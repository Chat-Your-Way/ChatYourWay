package com.chat.yourway.repository;

import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OnlineContactRepository {
  private static final Set<String> onlineContactEmails = ConcurrentHashMap.newKeySet();

  public void save(String email) {
    onlineContactEmails.add(email);
  }

  public void delete(String email) {
    onlineContactEmails.remove(email);
  }

  public boolean contains(String email) {
    return onlineContactEmails.contains(email);
  }
}
