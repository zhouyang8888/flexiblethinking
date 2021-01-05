package com.ft.flexiblethinking.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QueryQuestions implements IQueryQuestions {

    @Autowired
    private Repository repo;

    public List<Question> findAll() {
        return (List<Question>) repo.findAll();
    }

    public Optional<Question> findByID(Long id) {
        return repo.findById(id);
    }

}
