package com.ft.flexiblethinking.model.data;

import org.springframework.data.repository.CrudRepository;

@org.springframework.stereotype.Repository
public interface RepoQuestions extends CrudRepository<Question, Long> {
}
