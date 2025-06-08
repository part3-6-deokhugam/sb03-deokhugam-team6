package com.part3.deokhugam.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {
  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private int rating;

  @Column(nullable = false)
  @Builder.Default
  private boolean deleted = false;

//  @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
//  private ReviewMetrics metrics;
//
//  @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<Comment> comments = new ArrayList<>();
//
//  @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
//  private Set<ReviewLike> likes = new HashSet<>();
//
//  @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<Notification> notifications = new ArrayList<>();
//
//  @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<PopularReview> popularReviews = new ArrayList<>();
}