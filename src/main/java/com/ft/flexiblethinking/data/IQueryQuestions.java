package com.ft.flexiblethinking.data;

import java.util.List;
import java.util.Optional;

public interface IQueryQuestions {
    public List<Question> findAll();
    public QuestionStruct findByID(Long id);
    public void save(QuestionStruct quest);
    public void saveAll(List<QuestionStruct> quests);
    public long count();
}
