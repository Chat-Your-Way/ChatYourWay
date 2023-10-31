package com.chat.yourway.repository;

public interface OnlineContactRepository {
    /**
     * Saves a contact's email address to the repository.
     *
     * @param email The email address of the contact to be saved.
     */
    void save(String email);

    /**
     * Deletes a contact's email address from the repository.
     *
     * @param email The email address of the contact to be deleted.
     */
    void delete(String email);

    /**
     * Checks if a contact with the given email address exists in the repository.
     *
     * @param email The email address to check for existence.
     * @return true if the contact exists, false otherwise.
     */
    boolean contains(String email);
}
