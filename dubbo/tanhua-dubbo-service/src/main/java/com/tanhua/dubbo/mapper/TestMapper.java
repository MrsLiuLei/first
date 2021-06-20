package com.tanhua.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.domain.db.Answer;
import com.tanhua.domain.db.QuestionTest;
import com.tanhua.domain.db.Result;
import com.tanhua.domain.db.Test;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TestMapper{
    @Select("select qid from tb_text_question where tid=#{tid}")
    List<Integer> findByTid(Integer tid);
    @Select("select * from test_question where id=#{qid}")
    QuestionTest findByQid(Integer qid);
    @Select("select * from test_answer where qid=#{qid}")
    List<Answer> findByQid1(Integer qid);
    @Select("select * from tb_test limit 0,3")
    List<Test> find();
    @Select("select score from test_answer where id=#{id} and qid=#{qid}")
    Integer findScore(String id,String qid);
    @Select("select * from result where id=#{id}")
    Result findByRid(String id);
}
