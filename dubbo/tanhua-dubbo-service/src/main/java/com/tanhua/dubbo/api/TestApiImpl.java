package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Answer;
import com.tanhua.domain.db.QuestionTest;
import com.tanhua.domain.db.Result;
import com.tanhua.domain.db.Test;
import com.tanhua.domain.vo.AnswerVo1;
import com.tanhua.dubbo.mapper.TestMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class TestApiImpl implements TestApi{
    @Autowired
    private TestMapper testMapper;

    @Override
    public List<Integer> findByTid(Integer tid) {
        return testMapper.findByTid(tid);
    }

    @Override
    public QuestionTest findByQid(Integer qid) {
        return testMapper.findByQid(qid);
    }

    @Override
    public List<Answer> findByQid1(Integer qid) {
        return testMapper.findByQid1(qid);
    }

    @Override
    public List<Test> find() {
        return testMapper.find();
    }

    @Override
    public Integer countScore(AnswerVo1 answerVo1) {
        return testMapper.findScore(answerVo1.getOptionId(),answerVo1.getQuestionId());
    }

    @Override
    public Result findByRid(String id) {
        return testMapper.findByRid(id);
    }
}
