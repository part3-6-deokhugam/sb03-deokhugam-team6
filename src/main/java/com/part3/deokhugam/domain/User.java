package com.part3.deokhugam.domain;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.*;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @Email
  @NotBlank
  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @NotBlank
  @Size(min = 8, max = 100)
  @Column(nullable = false, length = 100)
  private String password;

  @NotBlank
  @Size(min = 2, max = 20)
  @Column(nullable = false, length = 20)
  private String nickname;

  @Column(name = "is_deleted", nullable = false)
  private boolean deleted = false;
  /* (다른 엔티티구현까지 기다림)
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Review> reviews = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Notification> notifications = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReviewLike> reviewLikes = new ArrayList<>();
   */
  
  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }

  public void delete() {
    this.deleted = true;
  }
}