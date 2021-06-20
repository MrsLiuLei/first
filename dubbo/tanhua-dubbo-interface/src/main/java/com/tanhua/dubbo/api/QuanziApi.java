package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import org.bson.types.ObjectId;

public interface QuanziApi {
    String add(PublishVo publishVo);
    void updateState(String id,Integer state);
    PageResult findByFriend(int page,int size,Long userId);
    PageResult findByRecommend(int page,int size,Long userId);
    PageResult findMyPublish(int page,int size,Long userId);
    long save(Comment comment);
    long delete(Comment comment);
    Publish findById(String publishId);
    PageResult findComment(int page,int size,String publishId);
    PageResult findComment(int page,int size,Integer commentType,Long publishUserId);
    Comment findById(Integer type, String id);
}
