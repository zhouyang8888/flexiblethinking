package com.ft.flexiblethinking.model.data;

import java.util.List;

public interface IQueryQuestions {
    public List<Question> findAll();
    public Question findByID(long id);
    public List<Question> findByID(Long start, Long end);
    public void save(Question quest);
    public int saveAll(List<Question> quests);
    public long count();
    public void deleteByID(long id);
}
