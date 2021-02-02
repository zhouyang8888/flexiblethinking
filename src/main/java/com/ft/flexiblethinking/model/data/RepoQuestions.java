package com.ft.flexiblethinking.model.data;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;


@org.springframework.stereotype.Repository
public interface RepoQuestions extends CrudRepository<Question, Long> {

    @Transactional
    @Modifying
    @Query("update Question q set q.content = :content, q.isvalid = :isvalid where q.id = :id")
    public void updateByID(@Param(value = "id") long id, @Param(value = "content") String content, @Param(value = "isvalid") boolean isValid);


    @Transactional
    @Modifying
    @Query("update Question q set q.isvalid = false where q.id = :id")
    public void markDeleteByID(@Param(value = "id") long id);
}
