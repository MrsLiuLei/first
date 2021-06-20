package com.tanhua.server.service;

import com.tanhua.domain.db.*;
import com.tanhua.domain.vo.*;
import com.tanhua.dubbo.api.TestApi;
import com.tanhua.dubbo.api.UserInfoApi;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestService {
    @Reference
    private TestApi testApi;
    @Reference
    private UserInfoApi userInfoApi;
    public List<TestVo> find(){
        List<Test> testList = testApi.find();
        ArrayList<TestVo> testVos = new ArrayList<>();
        if (testList!=null&&testList.size()>0){
            for (Test test : testList) {
                TestVo testVo = new TestVo();
                BeanUtils.copyProperties(test,testVo);
                testVo.setId(String.valueOf(test.getId()));
                ArrayList<QuestionVo> questionVos = new ArrayList<>();
                List<Integer> integers = testApi.findByTid(test.getId());
                if (integers!=null&&integers.size()>0){
                    for (Integer integer : integers) {
                        QuestionVo questionVo = new QuestionVo();
                        QuestionTest byQid = testApi.findByQid(integer);
                        BeanUtils.copyProperties(byQid,questionVo);
                        List<Answer> byQid1 = testApi.findByQid1(integer);
                        List<AnswerVo> answerVos = new ArrayList<>();
                        for (Answer answer : byQid1) {
                            AnswerVo answerVo = new AnswerVo();
                            answerVo.setId(answer.getId());
                            answerVo.setOption(answer.getOption());
                            answerVos.add(answerVo);
                        }
                        questionVo.setOptions(answerVos);
                        questionVos.add(questionVo);
                    }
                }
                testVo.setQuestions(questionVos);
                testVos.add(testVo);
            }
        }
        return testVos;
    }
    public String testResult(List<AnswerVo1> list){
        if (list!=null&&list.size()>0){
            Integer score=0;
            for (AnswerVo1 answerVo1 : list) {
                Integer score1 = testApi.countScore(answerVo1);
                score+=score1;
            }
            if (score<21){
                return "1";
            }else if (score<=40){
                return "2";
            }else if (score<=55){
                return "3";
            }else {
                return "4";
            }
        }
        return null;
    }
    public ResultVo findById(String id){
        ResultVo resultVo = new ResultVo();
        Result result = testApi.findByRid(id);
        BeanUtils.copyProperties(result,resultVo);
        List<UserInfo> list = userInfoApi.findByCid(id);
        ArrayList<SexVo> sexVos = new ArrayList<>();
        for (UserInfo userInfo : list) {
            SexVo sexVo = new SexVo();
            sexVo.setId(userInfo.getId().intValue());
            sexVo.setAvatar(userInfo.getAvatar());
            sexVos.add(sexVo);
        }
        resultVo.setSimilarYou(sexVos);
        List<ResultVo1> maps = new ArrayList<>();
        ResultVo1 vo = new ResultVo1("内向", "60");
        ResultVo1 vo1 = new ResultVo1("理性", "60");
        ResultVo1 vo11 = new ResultVo1("抽象", "60");
        ResultVo1 vo12 = new ResultVo1("外向", "60");
        maps.add(vo);
        maps.add(vo1);
        maps.add(vo11);
        maps.add(vo12);
        resultVo.setDimensions(maps);
        return resultVo;
    }
}
