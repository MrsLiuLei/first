package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.templates.HuanXinTemplate;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.ContactVo;
import com.tanhua.domain.vo.MessageVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.QuanziApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class IMService {
    @Autowired
    private HuanXinTemplate huanXinTemplate;
    @Reference
    private QuanziApi quanziApi;
    @Reference
    private FriendApi friendApi;
    @Reference
    private SettingsApi settingsApi;
    @Reference
    private UserInfoApi userInfoApi;
    public void replyQuestion(Map<String,Object> map){
        Long userId=Long.parseLong(map.get("userId").toString());
        String  reply =(String) map.get("reply");
        UserInfo userInfo = userInfoApi.findById(UserHolder.getUserId());
        Question question = settingsApi.findQuestionByUserId(userId);
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("userId", userInfo.getId().toString());
        map1.put("nickname", userInfo.getNickname());
        map1.put("strangerQuestion", question==null?"你喜欢我吗？":question.getTxt());
        map1.put("reply", reply);
        String s = JSON.toJSONString(map1);
        huanXinTemplate.sendMsg(userId.toString(),s);
    }
    public void addContacts(Long friendId){
        friendApi.add(UserHolder.getUserId(),friendId);
        huanXinTemplate.makeFriends(UserHolder.getUserId(),friendId);
    }
    public ResponseEntity findPage(Integer page, Integer size, String keyword){
        PageResult pageResult = friendApi.findPage(UserHolder.getUserId(), page, size, keyword);
        List<Friend> items = pageResult.getItems();
        ArrayList<ContactVo> list = new ArrayList<>();
        for (Friend friend : items) {
            UserInfo userInfo = userInfoApi.findById(friend.getFriendId());
            ContactVo contactVo = new ContactVo();
            BeanUtils.copyProperties(userInfo,contactVo);
            contactVo.setCity(StringUtils.substringBefore(userInfo.getCity(),"-"));
            contactVo.setUserId(userInfo.getId().toString());
            list.add(contactVo);
        }
        pageResult.setItems(list);
        return ResponseEntity.ok(pageResult);
    }
    public ResponseEntity findPageByUserId(int page,int size,Integer type){
        PageResult pageResult = quanziApi.findComment(page, size, type, UserHolder.getUserId());
        List<Comment> items = pageResult.getItems();
        ArrayList<MessageVo> messageVos = new ArrayList<>();
        for (Comment item : items) {
            MessageVo messageVo = new MessageVo();
            messageVo.setId(item.getId().toHexString());
            UserInfo userInfo = userInfoApi.findById(item.getUserId());
            messageVo.setAvatar(userInfo.getAvatar());
            messageVo.setNickname(userInfo.getNickname());
            messageVo.setCreateDate( new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(item.getCreated())));
            messageVos.add(messageVo);
        }
        pageResult.setItems(messageVos);
        return ResponseEntity.ok(pageResult);
    }
}
