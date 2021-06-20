package com.tanhua.server.service;

import com.tanhua.domain.db.User;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.dubbo.api.UserLikeApi;
import com.tanhua.server.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
public class UserLikeService {
    @Reference
    private UserLikeApi userLikeApi;
    public void add(Long likeUserId){
        UserLike userLike = new UserLike();
        userLike.setCreated(System.currentTimeMillis());
        userLike.setId(ObjectId.get());
        userLike.setUserId(UserHolder.getUserId());
        userLike.setLikeUserId(likeUserId);
        userLikeApi.add(userLike);
    }
    public void delete(Long likeUserId){
        userLikeApi.delete(UserHolder.getUserId(),likeUserId);
    }
}
