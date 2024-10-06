package com.chat.yourway.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@EqualsAndHashCode(of = {"id"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(schema = "chat", name = "contacts")
public class Contact implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Byte avatarId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 2048)
    private String password;

    @Column(nullable = false)
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isPermittedSendingPrivateMessage;

    @ManyToMany
    @JoinTable(
            schema = "chat",
            name = "contact_favorite_topics",
            joinColumns = @JoinColumn(name = "contact_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private Set<Topic> favoriteTopics = new HashSet<>();

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private List<Message> messages;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            schema = "chat",
            name = "unread_messages",
            joinColumns = @JoinColumn(name = "contact_id"),
            inverseJoinColumns = @JoinColumn(name = "message_id")
    )
    private List<Message> unreadMessages;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }
}
