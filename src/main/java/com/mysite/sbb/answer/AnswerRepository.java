package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    @Query("SELECT a " +
            "FROM Answer a " +
            "WHERE a.question = :question " +
            "ORDER BY SIZE(a.voter) DESC")
    Page<Answer> findByQuestionOrderByVoterDesc(@Param("question") Question question, Pageable pageable);
}
