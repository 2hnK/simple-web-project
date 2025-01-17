package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Set;

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
            throw new IllegalStateException("Comment must be associated with either a Question or an Answer, not both.");
        }
    }
}
