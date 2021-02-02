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

    public Question findByID(long id) {
        Optional<Question> oq = repo.findById(id);
        if (oq.isPresent())
            return oq.get();
        else
            return null;
    }

    public List<Question> findByID(Long start, Long end) {
        List<Long> ids = new ArrayList<>();
        for (Long s = start; s < end; s++) ids.add(s);
        Iterable<Question> lq = repo.findAllById(ids);
        List<Question> ret = new ArrayList<>();
        Iterator<Question> lqItr = lq.iterator();
        while (lqItr.hasNext()) {
            ret.add(lqItr.next());
        }
        return ret;
    }

    public void save(Question question) {
        repo.save(question);
    }

    public int saveAll(List<Question> qs) {
        return ((List<Question>)repo.saveAll(qs)).size();
    }

    public long count() {
        return repo.count();
    }


    public void deleteByID(long id) {
        repo.deleteById(id);
    }

    public void updateByID(long pid, String content, boolean isValid) {
        repo.updateByID(pid, content, isValid);
    }

    public void markDeleteByID(long id) {
        repo.markDeleteByID(id);
    }
}
