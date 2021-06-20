package com.tanhua.server.service;

import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.RecommendQueryParam;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.dubbo.api.QuanziApi;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.UserHolder;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class TodayBestService {
    @Reference
    private SettingsApi settingsApi;
    @Reference
    private RecommendUserApi recommendUserApi;
    @Reference
    private UserInfoApi userInfoApi;
    //查询今日佳人
    public ResponseEntity findTodayBest(){
        Long id = UserHolder.getUser().getId();
        RecommendUser recommendUser = recommendUserApi.findMaxScore(id);
        if (recommendUser==null){
            recommendUser=new RecommendUser();
            recommendUser.setUserId(2L);
            recommendUser.setScore(95d);
        }
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());
        TodayBestVo todayBestVo = new TodayBestVo();
        BeanUtils.copyProperties(userInfo,todayBestVo);
        todayBestVo.setFateValue(recommendUser.getScore().intValue());
        if (!StringUtils.isEmpty(userInfo.getTags())){
            todayBestVo.setTags(userInfo.getTags().split(","));
        }
        return ResponseEntity.ok(todayBestVo);
    }
    //查询推荐用户
    public ResponseEntity RecommendUser(RecommendQueryParam queryParam){
        PageResult result = recommendUserApi.findPage(queryParam.getPage(), queryParam.getPagesize(), UserHolder.getUserId());
        List<RecommendUser> items = result.getItems();
        if (CollectionUtils.isEmpty(items)){
            result = new PageResult(10l,queryParam.getPagesize().longValue(),1l,1l,null);
            items=defaultRecommend();
        }
        ArrayList<TodayBestVo> todayBestVos = new ArrayList<>();
        for (RecommendUser vo : items) {
            UserInfo userInfo = userInfoApi.findById(vo.getUserId());
            TodayBestVo todayBestVo = new TodayBestVo();
            BeanUtils.copyProperties(userInfo,todayBestVo);
            todayBestVo.setTags(StringUtils.split(userInfo.getTags(),","));
            todayBestVo.setFateValue(vo.getScore().intValue());
            todayBestVos.add(todayBestVo);
        }
        result.setItems(todayBestVos);
        return ResponseEntity.ok(result);
    }
    private List<RecommendUser> defaultRecommend() {
        String ids = "1,2,3,4,5,6,7,8,9,10";
        List<RecommendUser> records = new ArrayList<>();
        for (String id : ids.split(",")) {
            RecommendUser recommendUser = new RecommendUser();
            recommendUser.setUserId(Long.valueOf(id));
            recommendUser.setScore(RandomUtils.nextDouble(70, 98));
            records.add(recommendUser);
        }
        return records;
    }
    //查询推荐用户详情
    public ResponseEntity getTodayUserInfo(Long userId){
        UserInfo userInfo = userInfoApi.findById(userId);
        TodayBestVo todayBestVo = new TodayBestVo();
        BeanUtils.copyProperties(userInfo,todayBestVo);
        Double score = recommendUserApi.findScore(userId, UserHolder.getUserId());
        if (score==null){
            todayBestVo.setFateValue(801);
        }else {
            todayBestVo.setFateValue(score.intValue());
        }
        todayBestVo.setTags(userInfo.getTags().split(","));
        return ResponseEntity.ok(todayBestVo);
    }
    //查询陌生人问题
    public ResponseEntity getQuestion(Long userId){
        Question question = settingsApi.findQuestionByUserId(userId);
        if(null == question || StringUtils.isEmpty(question.getTxt())){
            return ResponseEntity.ok("你喜欢我吗?");
        }
        return ResponseEntity.ok(question.getTxt());
    }
}
