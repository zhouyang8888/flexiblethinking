package com.ft.flexiblethinking.model.data;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public void save(QuestionStruct quest) {
        Question q = new Question();
        q.setContent(gson.toJson(quest));
        repo.save(q);
    }

    public void saveAll(List<QuestionStruct> quests) {
        List<Question> qs = Collections.EMPTY_LIST;
        for (QuestionStruct qstruct : quests) {
            Question q = new Question();
            q.setContent(gson.toJson(qstruct));
            qs.add(q);
        }
        repo.saveAll(qs);
    }

    public long count() {
        return repo.count();
    }

}
