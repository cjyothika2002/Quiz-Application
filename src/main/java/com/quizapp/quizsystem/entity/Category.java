package com.quizapp.quizsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Categories are dynamic and DB-backed (Section 3/4) — never hard-coded
 * in Java. Admin creates/updates/deletes these via REST APIs.
 */
@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    // No back-reference (@OneToMany) to Quiz here on purpose — avoids
    // circular serialization risk and isn't needed for navigation from
    // this side. Quiz -> Category lookups go through QuizRepository instead.
}
