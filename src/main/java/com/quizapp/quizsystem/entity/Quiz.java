package com.quizapp.quizsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * A Quiz belongs to a Category and holds a "question bank" configuration.
 * The actual Question entities reference this quiz's id (Section 6/7/8).
 */
@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * How many questions are randomly drawn from this quiz's question bank
     * for a single attempt (Section 7). Admin-configured, must be >= 1.
     */
    @Min(1)
    @Column(nullable = false)
    private int questionsPerAttempt;

    /**
     * A quiz is invisible/unavailable to users until published = true.
     * The service layer must validate the question-count rule (Section 8)
     * before allowing this flag to flip to true.
     */
    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
