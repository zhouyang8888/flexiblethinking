package com.ft.flexiblethinking.model.submission;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RepoSubmissions extends CrudRepository<Submission, Long> {

    @Query(value="SELECT o.id FROM submission o WHERE o.uid=?1 AND o.qid=?2", nativeQuery = true)
    public List<Long> listSubmissionsByUserIDAndQuestionID(long uid, long qid);

    @Query(value="SELECT o.id FROM submission o WHERE o.uid=?1", nativeQuery = true)
    public List<Long> listSubmissionsByUserID(long uid);

    @Query(value="SELECT o.id FROM submission o WHERE o.qid=?1", nativeQuery = true)
    public List<Long> listSubmissionsByQuestionID(long qid);
    
}
