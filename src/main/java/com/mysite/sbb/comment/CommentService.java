package com.mysite.sbb.comment;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public Comment create(Question question, String content, SiteUser author) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setQuestion(question);
        comment.setAuthor(author);
        this.commentRepository.save(comment);
        return comment;
    }

    public Comment create(Answer answer, String content, SiteUser author) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setAnswer(answer);
        comment.setAuthor(author);
        this.commentRepository.save(comment);
        return comment;
    }

    public void modify(Comment comment, String content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }

    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    }

    public Comment getQuestionComment(Question question, Integer commentId) {
        Optional<Comment> comment = this.commentRepository.findById(commentId);
        if (comment.isPresent()) {
            Comment foundComment = comment.get();
            if (foundComment.getQuestion() != null &&
                    foundComment.getQuestion().getId().equals(question.getId())) {
                return foundComment;
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "해당 질문의 댓글이 아닙니다.");
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public Comment getAnswerComment(Answer answer, Integer commentId) {
        Optional<Comment> comment = this.commentRepository.findById(commentId);
        if (comment.isPresent()) {
            Comment foundComment = comment.get();
            if (foundComment.getAnswer() != null &&
                    foundComment.getAnswer().getId().equals(answer.getId())) {
                return foundComment;
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "해당 답변의 댓글이 아닙니다.");
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public void vote(Comment comment, SiteUser siteUser) {
        comment.getVoter().add(siteUser);
        this.commentRepository.save(comment);
    }
}
