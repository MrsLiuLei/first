package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Answer;
import com.tanhua.domain.db.QuestionTest;
import com.tanhua.domain.db.Result;
import com.tanhua.domain.db.Test;
import com.tanhua.domain.vo.AnswerVo1;

import java.util.List;

public interface TestApi {
    List<Integer> findByTid(Integer tid);
    QuestionTest findByQid(Integer qid);
    List<Answer> findByQid1(Integer qid);
    List<Test> find();
    Integer countScore(AnswerVo1 answerVo1);
    Result findByRid(String id);
}
