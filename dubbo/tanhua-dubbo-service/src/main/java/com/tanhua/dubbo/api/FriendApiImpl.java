package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.vo.PageResult;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


@Service
public class FriendApiImpl implements FriendApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void add(Long userId, Long friendId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId).and("friendId").is(friendId));
        if (!mongoTemplate.exists(query, Friend.class)){
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            friend.setId(ObjectId.get());
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }
    }

    @Override
    public PageResult findPage(Long userId, Integer page, Integer size, String keyword) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)).limit(size).skip((page-1)*size);
        long count = mongoTemplate.count(query, Friend.class);
        List<Friend> list = mongoTemplate.find(query, Friend.class);
        return new PageResult(count,(long)size,(long)(Math.ceil((1.0*count)/size)),(long)page,list);
    }
}
