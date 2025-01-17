package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/comment")
@Controller
@RequiredArgsConstructor
public class CommentController {
    private final UserService userService;
    private final CommentService commentService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/question{questionId}")
    public String createCommentForQuestion(Model model,
                                @PathVariable("questionId") Integer id,
                                @Valid CommentForm commentForm,
                                BindingResult bindingResult,
                                Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            model.addAttribute("commentForm", commentForm);
            return "question_detail";
        }

        Comment comment = this.commentService.create(question, commentForm.getContent(), siteUser);
        return String.format("redirect:/question/detail/%s#comment_%s",
                question.getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer{answerId}")
    public String createCommentForAnswer(Model model,
                                           @PathVariable("answerId") Integer answerId,
                                           @Valid CommentForm commentForm,
                                           BindingResult bindingResult,
                                           Principal principal) {
        Answer answer = this.answerService.getAnswer(answerId);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", answer.getQuestion());
            model.addAttribute("commentForm", commentForm);
            return "question_detail";
        }

        Comment comment = this.commentService.create(answer, commentForm.getContent(), siteUser);
        return String.format("redirect:/question/detail/%s#comment_%s",
                answer.getQuestion().getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/question{questionId}_{commentId}")
    public String deleteCommentForQuestion(Principal principal,
                                @PathVariable("questionId") Integer questionId,
                                @PathVariable("commentId") Integer CommentId) {
        Question question = this.questionService.getQuestion(questionId);
        Comment comment = this.commentService.getComment(question, CommentId);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/question/detail/%s", question.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/answer{answerId}_{commentId}")
    public String deleteCommentForAnswer(Principal principal,
                                @PathVariable("answerId") Integer answerId,
                                @PathVariable("commentId") Integer CommentId) {
        Answer answer = this.answerService.getAnswer(answerId);
        Comment comment = this.commentService.getComment(answer, CommentId);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/question{questionId}_{commentId}")
    public String voteCommentForQuestion(Principal principal,
                                @PathVariable("questionId") Integer questionId,
                                @PathVariable("commentId") Integer commentId) {
        Question question = this.questionService.getQuestion(questionId);
        Comment comment = this.commentService.getComment(question, commentId);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.commentService.vote(comment, siteUser);
        return String.format("redirect:/question/detail/%s#comment_%s", question.getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/answer{answerId}_{commentId}")
    public String voteCommentForAnswer(Principal principal,
                                         @PathVariable("answerId") Integer answerId,
                                         @PathVariable("commentId") Integer commentId) {
        Answer answer = this.answerService.getAnswer(answerId);
        Comment comment = this.commentService.getComment(answer, commentId);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.commentService.vote(comment, siteUser);
        return String.format("redirect:/question/detail/%s#comment_%s", answer.getQuestion().getId(), comment.getId());
    }
}
