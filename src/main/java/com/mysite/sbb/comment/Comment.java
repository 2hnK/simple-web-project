package com.mysite.sbb.comment;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content; // 답변 내용

    @CreatedDate
    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @ManyToOne
    private SiteUser author;

    @ManyToOne
    private Question question;

    @ManyToOne
    private Answer answer;

    @ManyToMany
    private Set<SiteUser> voter;

    @PrePersist // 엔티티가 Persist 되기 전에 실행하는 메서드
    @PreUpdate // 엔티티가 Update 되기 전에 실행하는 메서드
    private void validateAssociation() {
        if ((question == null && answer == null) || (question != null && answer != null)) {
            throw new IllegalStateException(
                    "Comment must be associated with either a Question or an Answer, not both.");
        }
    }
}
