package com.ft.flexiblethinking.model.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuerySubmissions {
    @Autowired
    RepoSubmissions repo;

    public List<Long> listByUidAndQid(long uid, long qid) {
        return repo.listSubmissionsByUserIDAndQuestionID(uid, qid);
    }

    public List<Long> listByUid(long uid) {
        return repo.listSubmissionsByUserID(uid);
    }

    public List<Long> listByQid(long qid) {
        return repo.listSubmissionsByQuestionID(qid);
    }

    public Submission findByID(long id) {
        Optional<Submission> opSub = repo.findById(id);
        if (opSub.isPresent()) return opSub.get();
        else return null;
    }

    public void add(Submission sub) {
        repo.save(sub);
    }

    public void deleteByID(long id) {
        repo.deleteById(id);
    }
}
