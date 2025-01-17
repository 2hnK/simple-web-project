package com.mysite.sbb.answer;

import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Answer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; // 답변 고유 번호

	@Column(columnDefinition = "TEXT")
	private String content; // 답변 내용

	@CreatedDate
	private LocalDateTime createDate; // 답변 작성 날짜

	@ManyToOne
	private Question question; // 연관 질문

	@ManyToOne
	private SiteUser author;

	private LocalDateTime modifyDate;

	@ManyToMany
	private Set<SiteUser> voter;

	@OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
	private List<Comment> commentList;
}
