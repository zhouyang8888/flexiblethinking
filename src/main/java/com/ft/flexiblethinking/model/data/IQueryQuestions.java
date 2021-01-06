package com.ft.flexiblethinking.model.data;

import java.util.List;

public interface IQueryQuestions {
    public List<Question> findAll();
    public QuestionStruct findByID(Long id);
    public void save(QuestionStruct quest);
    public void saveAll(List<QuestionStruct> quests);
    public long count();
}
