package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.comment.CommentForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/question")
@Controller
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;

    /*
     * Model 객체는 Spring MVC에서 View 로 데이터를 전달하기 위해 사용하는 인터페이스이다.
     */
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Question> paging = this.questionService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "question_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") Integer id,
                         AnswerForm answerForm,
                         CommentForm commentForm,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "sort", defaultValue = "voter") String sort) {

        Question question = this.questionService.getQuestion(id);
        Page<Answer> paging = sort.equals("voter")
                ? this.answerService.getAnswerOrderByVoterDesc(page, question)
                : this.answerService.getAnswerOrderByCreateDateDesc(page, question);

        model.addAttribute("paging", paging);
        model.addAttribute("question", question);

        return "question_detail";
    }

//    @GetMapping(value = "/detail/{id}")
//    public String detail(Model model,
//                         @PathVariable("id") Integer id,
//                         AnswerForm answerForm) {
//        Question question = this.questionService.getQuestion(id);
//        model.addAttribute("question", question);
//        return "question_detail";
//    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

    // @Valid 애너테이션으로 검증된 subject, content를 갖는QuestionForm 객체를 전달한다
    // BidingResult 매개변수는 @Valid 애너테이션으로 검증이 수행된 객체를 의미한다.
    // @PreAuthorize("isAuthenicated()") 로그인한 사용자만 호출할 수 있다.
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm,
                                 BindingResult bindingResult,
                                 Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm,
                                 @PathVariable("id") Integer id,
                                 Principal principal) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm,
                                 BindingResult bindingResult,
                                 Principal principal,
                                 @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal,
                                 @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal,
                               @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
}
