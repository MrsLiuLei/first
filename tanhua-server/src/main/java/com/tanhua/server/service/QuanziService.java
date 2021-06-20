package com.tanhua.server.service;

import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.mongo.Visitor;
import com.tanhua.domain.vo.*;
import com.tanhua.dubbo.api.QuanziApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VisitorApi;
import com.tanhua.server.UserHolder;
import com.tanhua.server.utils.RelativeDateFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class QuanziService {
    @Autowired
    private OssTemplate ossTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Reference
    private VisitorApi visitorApi;
    @Reference
    private UserInfoApi userInfoApi;
    @Reference
    private QuanziApi quanziApi;
    public void save(PublishVo publishVo, MultipartFile[] multipartFiles) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String filename = multipartFile.getOriginalFilename();
            String s = ossTemplate.upload(filename, multipartFile.getInputStream());
            list.add(s);
        }
        publishVo.setUserId(UserHolder.getUserId());
        publishVo.setMedias(list);
        String publishId= quanziApi.add(publishVo);
        try {
            rocketMQTemplate.convertAndSend("tanhua-publish",publishId);
        } catch (MessagingException e) {
            log.error("发送MQ消息失败",e);
        }
    }
    public ResponseEntity findFriendPublish(int page,int size) {
        Long userId = UserHolder.getUserId();
        PageResult pageResult = quanziApi.findByFriend(page, size, userId);
        List<Publish> items = (List<Publish>) pageResult.getItems();
        ArrayList<MomentVo> voArrayList = new ArrayList<>();
        if (items != null) {
            for (Publish publish : items) {
                MomentVo vo = new MomentVo();
                BeanUtils.copyProperties(publish, vo);
                UserInfo userInfo = userInfoApi.findById(publish.getUserId());
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo, vo);
                    if (userInfo.getTags() != null) {
                        vo.setTags(userInfo.getTags().split(","));
                    }
                }
                vo.setId(publish.getId().toHexString());
                vo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));
                String key="publish_like_"+userId+"_"+publish.getId().toHexString();
                if (redisTemplate.opsForValue().get(key)!=null){
                    vo.setHasLiked(1);
                }else {
                    vo.setHasLiked(0);
                }
                vo.setHasLoved(0);
                vo.setImageContent(publish.getMedias().toArray(new String[]{}));
                vo.setDistance("50米");
                voArrayList.add(vo);
            }
        }
        pageResult.setItems(voArrayList);
        return ResponseEntity.ok(pageResult);
    }
    public ResponseEntity findRecommend(int page,int size){
        Long userId = UserHolder.getUserId();
        PageResult pageResult = quanziApi.findByRecommend(page, size,userId);
        List<Publish> items = pageResult.getItems();
        ArrayList<MomentVo> momentVos = new ArrayList<>();
        if (items!=null){
            for (Publish item : items) {
                MomentVo vo = new MomentVo();
                BeanUtils.copyProperties(item,vo);
                UserInfo userInfo = userInfoApi.findById(item.getUserId());
                if (userInfo!=null){
                    BeanUtils.copyProperties(userInfo,vo);
                    if (userInfo.getTags()!=null){
                        vo.setTags(userInfo.getTags().split(","));
                    }
                }
                vo.setId(item.getId().toHexString());
                vo.setCreateDate(RelativeDateFormat.format(new Date(item.getCreated())));
                vo.setHasLiked(0);
                String key="publish_like_"+userId+"_"+item.getId().toHexString();
                if (redisTemplate.opsForValue().get(key)!=null){
                    vo.setHasLoved(1);
                }else {
                    vo.setHasLoved(0);
                }
                vo.setImageContent(item.getMedias().toArray(new String[]{}));
                vo.setDistance("50米");
                momentVos.add(vo);
            }
        }
        pageResult.setItems(momentVos);
        return ResponseEntity.ok(pageResult);
    }
    public ResponseEntity findMyPublish(int page,int size,Long userId){
        PageResult pageResult = quanziApi.findMyPublish(page, size, userId);
        List<Publish> items = pageResult.getItems();
        ArrayList<MomentVo> momentVos = new ArrayList<>();
        if (items!=null){
            for (Publish item : items) {
                MomentVo vo = new MomentVo();
                BeanUtils.copyProperties(item,vo);
                UserInfo userInfo = userInfoApi.findById(item.getUserId());
                if (userInfo!=null){
                    BeanUtils.copyProperties(userInfo,vo);
                    if (userInfo.getTags()!=null){
                        vo.setTags(userInfo.getTags().split(","));
                    }
                }
                vo.setId(item.getId().toHexString());
                vo.setCreateDate(RelativeDateFormat.format(new Date(item.getCreated())));
                vo.setHasLiked(0);
                vo.setHasLoved(0);
                vo.setImageContent(item.getMedias().toArray(new String[]{}));
                vo.setDistance("50米");
                momentVos.add(vo);
            }
        }
        pageResult.setItems(momentVos);
        return ResponseEntity.ok(pageResult);
    }
    public Long like(String publishId){
        Long userId = UserHolder.getUserId();
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPublishId(new ObjectId(publishId));
        comment.setCommentType(1);
        comment.setPubType(1);
        comment.setPublishUserId(quanziApi.findById(publishId).getUserId());
        long l = quanziApi.save(comment);
        String key="publish_like_"+userId+"_"+publishId;
        redisTemplate.opsForValue().set("key",1);
        return l;
    }
    public Long unlike(String publishId){
        Long userId = UserHolder.getUserId();
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));
        comment.setCommentType(1);
        comment.setPubType(1);
        comment.setUserId(userId);
        long delete = quanziApi.delete(comment);
        String key="publish_like_"+userId+"_"+publishId;
        redisTemplate.delete(key);
        return delete;
    }
    public Long love(String publishId){
        Long userId = UserHolder.getUserId();
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));
        comment.setCommentType(3);
        comment.setPubType(1);
        comment.setUserId(userId);
        comment.setPublishUserId(quanziApi.findById(publishId).getUserId());
        long l = quanziApi.save(comment);
        String key = "publish_love_" + userId+"_" + publishId;
        redisTemplate.opsForValue().set(key,"1");
        return l;
    }
    public Long unlove(String publishId){
        Long userId = UserHolder.getUserId();
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));
        comment.setCommentType(3);
        comment.setPubType(1);
        comment.setUserId(userId);
        long l = quanziApi.delete(comment);
        String key = "publish_love_" + userId+"_" + publishId;
        redisTemplate.delete(key);
        return l;
    }
    public ResponseEntity findPublishById(String publishId){
        Publish publish = quanziApi.findById(publishId);
        MomentVo vo = new MomentVo();
        UserInfo userInfo = userInfoApi.findById(publish.getUserId());
        if (userInfo!=null){
            BeanUtils.copyProperties(userInfo,vo);
            if (userInfo.getTags()!=null){
                vo.setTags(userInfo.getTags().split(","));
            }
        }
        BeanUtils.copyProperties(publish,vo);
        vo.setId(publish.getId().toHexString());
        vo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));
        vo.setHasLiked(0);  //是否点赞  0：未点 1:点赞
        vo.setHasLoved(0);  //是否喜欢  0：未点 1:点赞
        vo.setImageContent(publish.getMedias().toArray(new String[]{}));
        vo.setDistance("50米");
        return ResponseEntity.ok(vo);
    }
    public List<VisitorVo> queryVisitors(){
        Long userId = UserHolder.getUserId();
        String key = "visitors_time_"+userId;
        String time = (String)redisTemplate.opsForValue().get(key);
       List<Visitor> visitors = new ArrayList<>();
        if (!StringUtils.isEmpty(time)){
            visitors = visitorApi.queryVisitors(time, userId);
        }else {
            visitors=visitorApi.queryVisitors(userId);
        }
        redisTemplate.opsForValue().set(key,System.currentTimeMillis()+"",5, TimeUnit.HOURS);
        ArrayList<VisitorVo> visitorVos = new ArrayList<>();
        if (visitors!=null&&visitors.size()>0){
            for (Visitor visitor : visitors) {
                VisitorVo visitorVo = new VisitorVo();
                UserInfo userInfo = userInfoApi.findById(visitor.getVisitorUserId());
                BeanUtils.copyProperties(userInfo,visitorVo);
                visitorVo.setTags(StringUtils.split(userInfo.getTags(),","));
                visitorVos.add(visitorVo);
            }
        }
        return visitorVos;
    }
}
