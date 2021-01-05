package com.ft.flexiblethinking.data;

import java.util.List;
import java.util.Optional;

public interface IQueryQuestions {
    public List<Question> findAll();
    public Optional<Question> findByID(Long id);
}
