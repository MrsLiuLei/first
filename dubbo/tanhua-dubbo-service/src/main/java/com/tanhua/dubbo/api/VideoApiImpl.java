package com.tanhua.dubbo.api;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.utils.IdService;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


@Service
public class VideoApiImpl implements VideoApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdService idService;
    @Override
    public void save(Video video) {
        video.setId(ObjectId.get());
        video.setCreated(System.currentTimeMillis());
        video.setVid(idService.nextId("video"));
        mongoTemplate.save(video);
    }
    @Override
    public PageResult findPage(int page, int size) {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("created"))).limit(size).skip((page-1)*size);
        long count = mongoTemplate.count(query, Video.class);
        List<Video> list = mongoTemplate.find(query, Video.class);
        return new PageResult(count,(long)size,(long)Math.ceil((count*1.0)/size),(long)page,list);
    }

    @Override
    public void add(FollowUser followUser) {
        followUser.setId(ObjectId.get());
        followUser.setCreated(System.currentTimeMillis());
         mongoTemplate.save(followUser);
    }

    @Override
    public void delete(FollowUser followUser) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(followUser.getUserId()).and("followUserId").is(followUser.getFollowUserId()));
        mongoTemplate.remove(query,FollowUser.class);
    }

    @Override
    public PageResult findAll(int page, int size, Long uid) {
        Query query = new Query(Criteria.where("userId").is(uid)).skip((page-1)*size).limit(size).with(Sort.by(Sort.Order.desc("created")));
        List<Video> list = mongoTemplate.find(query, Video.class);
        long count = mongoTemplate.count(query, Video.class);
        return new PageResult(count,(long)size,(long)Math.ceil((1.0*count)/size),(long)page,list);
    }
}
