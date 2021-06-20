package com.tanhua.server.service;

import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Setting;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.server.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SettingService {
    @Reference
    private SettingsApi settingsApi;
    public ResponseEntity findSetting(){
        User user = UserHolder.getUser();
        SettingsVo vo = new SettingsVo();
        Setting setting = settingsApi.findByUserId(user.getId());
        if (setting!=null){
            BeanUtils.copyProperties(setting,vo);
        }
        Question question = settingsApi.findQuestionByUserId(user.getId());
        if (question!=null){
            vo.setStrangerQuestion(question.getTxt());
        }
        vo.setPhone(user.getMobile());
        return ResponseEntity.ok(vo);
    }
    public ResponseEntity updateNotification(boolean like,boolean pinglun,boolean gonggao){
        Setting setting = settingsApi.findByUserId(UserHolder.getUserId());
        if (setting==null){
            setting = new Setting();
            setting.setUserId(UserHolder.getUserId());
            setting.setLikeNotification(like);
            setting.setGonggaoNotification(gonggao);
            setting.setPinglunNotification(pinglun);
            settingsApi.add(setting);
        }else {
            setting.setLikeNotification(like);
            setting.setGonggaoNotification(gonggao);
            setting.setPinglunNotification(pinglun);
            settingsApi.update(setting);
        }
        return ResponseEntity.ok(null);
    }
    public ResponseEntity saveQuestions(String content){
        Question question = settingsApi.findQuestionByUserId(UserHolder.getUserId());
        if (question==null){
            question = new Question();
            question.setUserId(UserHolder.getUserId());
            question.setTxt(content);
            settingsApi.aaQuestion(question);
        }else {
            question.setTxt(content);
            settingsApi.updateQuestion(question);
        }
        return ResponseEntity.ok(null);
    }
    public ResponseEntity findBlackList(int page,int pagesize){
        Long id = UserHolder.getUser().getId();
        PageResult<UserInfo> blackList = settingsApi.findBlackList(page, pagesize, id);
        return ResponseEntity.ok(blackList);
    }
    public ResponseEntity deleteBlack(Long deleteUserId){
        Long id = UserHolder.getUser().getId();
        settingsApi.deleteBlack(id,deleteUserId);
        return ResponseEntity.ok(null);
    }
}
