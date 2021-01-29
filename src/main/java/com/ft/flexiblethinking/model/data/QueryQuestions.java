package com.ft.flexiblethinking.model.data;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QueryQuestions implements IQueryQuestions {

    @Autowired
    private RepoQuestions repo;
    Gson gson = new Gson();

    public List<Question> findAll() {
        return (List<Question>) repo.findAll();
    }

    public QuestionStruct findByID(Long id) {
        Optional<Question> oq = repo.findById(id);
        if (oq.isPresent())
            return gson.fromJson(oq.get().getContent(), QuestionStruct.class);
        else
            return null;
    }

    public List<QuestionStruct> findByID(Long start, Long end) {
        List<Long> ids = new ArrayList<>();
        for (Long s = start; s < end; s++) ids.add(s);
        Iterable<Question> lq = repo.findAllById(ids);
        List<QuestionStruct> ret = new ArrayList<>();
        Iterator<Question> lqItr = lq.iterator();
        while (lqItr.hasNext()) {
            Question qu = lqItr.next();
            ret.add(gson.fromJson(qu.getContent(), QuestionStruct.class));
        }
        return ret;
    }

    public void save(QuestionStruct quest) {
        Question q = new Question();
        q.setContent(gson.toJson(quest));
        repo.save(q);
    }

    public int saveAll(List<QuestionStruct> quests) {
        List<Question> qs = new LinkedList<>();
        for (QuestionStruct qstruct : quests) {
            Question q = new Question();
            q.setContent(gson.toJson(qstruct));
            qs.add(q);
        }
        return ((List<Question>)repo.saveAll(qs)).size();
    }

    public long count() {
        return repo.count();
    }

}
