package com.tanhua.server.service;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.CommentVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.QuanziApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommentsService {
    @Reference
    private QuanziApi quanziApi;
    @Reference
    private UserInfoApi userInfoApi;
    @Autowired
    private RedisTemplate redisTemplate;
    public ResponseEntity findComment(String movementId, int page, int pagesize){
        PageResult comment = quanziApi.findComment(page, pagesize, movementId);
        List<Comment> items = comment.getItems();
        ArrayList<CommentVo> commentVos = new ArrayList<>();
        if (items!=null&&items.size()>0){
            for (Comment item : items) {
                CommentVo commentVo = new CommentVo();
                BeanUtils.copyProperties(item,commentVo);
                UserInfo userInfo = userInfoApi.findById(item.getUserId());
                BeanUtils.copyProperties(userInfo,commentVo);
                commentVo.setId(item.getId().toHexString());
                commentVo.setCreateDate(new DateTime(item.getCreated()).toString("yyyy年MM月dd日 HH:mm"));
                String key = "comment_like_" + UserHolder.getUserId()+"_" + item.getId().toHexString();
// 记录下点了赞了
                if(redisTemplate.hasKey(key)){
                    commentVo.setHasLiked(1);//是否点赞
                }
                else {
                    commentVo.setHasLiked(0);//是否点赞
                }
                commentVos.add(commentVo);
            }
        }
        comment.setItems(commentVos);
        return ResponseEntity.ok(comment);
    }
    public void addComment(Map<String,String> map){
        String comment = map.get("comment");
        String movementId = map.get("movementId");
        Comment comment1 = new Comment();
        comment1.setPublishId(new ObjectId(movementId));
        comment1.setCommentType(2);
        comment1.setPubType(1);
        comment1.setContent(comment);
        comment1.setUserId(UserHolder.getUserId());
        comment1.setPublishUserId(quanziApi.findById(movementId).getUserId());
        quanziApi.save(comment1);
    }
    public Long likeComment(String commentId){
        Long userId = UserHolder.getUserId();
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setCommentType(1);
        comment.setPubType(3);
        comment.setId(new ObjectId(commentId));
        Comment comment2 = quanziApi.findById(1, commentId);
        ObjectId publishId = comment2.getPublishId();
        Publish publish = quanziApi.findById(publishId.toString());
        comment.setPublishUserId(publish.getUserId());
        long l = quanziApi.save(comment);
        String key = "comment_like_" + userId+"_" + commentId;
        // 记录下点了赞了
        redisTemplate.opsForValue().set(key,"1");
        return l;
    }
    public Long unlikeComment(String commentId){
        Long userId = UserHolder.getUserId();
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setId(new ObjectId(commentId));
        comment.setPubType(3);
        comment.setCommentType(1);
        long l = quanziApi.delete(comment);
        String key = "comment_like_" + userId+"_" + commentId;
        // 移除redis中记录
        redisTemplate.delete(key);
        return l;
    }
}
