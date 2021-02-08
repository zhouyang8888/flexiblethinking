package com.ft.flexiblethinking.model.submission;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;

public interface RepoSubmissions extends CrudRepository<Submission, Long> {

    @Query(value="SELECT o.id FROM submission o WHERE o.uid=?1 AND o.qid=?2", nativeQuery = true)
    public List<Long> listSubmissionsByUserIDAndQuestionID(long uid, long qid);

    @Query(value="SELECT o.id FROM submission o WHERE o.uid=?1", nativeQuery = true)
    public List<Long> listSubmissionsByUserID(long uid);

    @Query(value="SELECT o.id FROM submission o WHERE o.qid=?1", nativeQuery = true)
    public List<Long> listSubmissionsByQuestionID(long qid);

    @Query(value="SELECT COUNT(IF(o.isOK=1, 1, null)) AS numSubmitOK, COUNT(1) AS numSubmits FROM submission o WHERE uid=?1", nativeQuery = true)
    public Map<String, Long> countSubmissionRatio(long uid);

    @Query(value="select sum(c) as numProbOK, sum(t) as numProb from (select max(if(isok=1, 1, 0)) as c, 1 as t from submission where uid=?1 group by qid) s", nativeQuery = true)
    public Map<String, Long> countQuestionRatio(long uid);
}
