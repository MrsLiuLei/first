package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.*;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.dubbo.utils.IdService;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuanziApiImpl implements QuanziApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdService idService;
    @Override
    public String  add(PublishVo publishVo) {
        long l = System.currentTimeMillis();
        Publish publish = new Publish();
        BeanUtils.copyProperties(publishVo,publish);
        publish.setId(ObjectId.get());
        publish.setPid(idService.nextId("quanzi_publish"));
        publish.setSeeType(1);
        publish.setCreated(l);
        publish.setLocationName(publishVo.getLocation());
        mongoTemplate.save(publish);
        Album album = new Album();
        album.setCreated(l);
        album.setPublishId(publish.getId());
        album.setId(ObjectId.get());
        mongoTemplate.save(album,"quanzi_album_" + publish.getUserId());
        Query query = Query.query(Criteria.where("userId").is(publishVo.getUserId()));
        List<Friend> friends = mongoTemplate.find(query, Friend.class);
        for (Friend friend : friends) {
            Long friendId = friend.getFriendId();
            TimeLine timeLine = new TimeLine();
            timeLine.setId(ObjectId.get());
            timeLine.setUserId(publish.getUserId());
            timeLine.setPublishId(publish.getId());
            timeLine.setCreated(l);
            mongoTemplate.save(timeLine,"quanzi_time_line_"+friendId);
        }
        return publish.getId().toHexString();
    }

    @Override
    public void updateState(String id, Integer state) {
        Query query = new Query();
        query.addCriteria(Criteria.where(id).is(id));
        Update update = new Update();
        update.set("state",state);
        mongoTemplate.updateFirst(query,update,Publish.class);
    }

    @Override
    public PageResult findByFriend(int page, int size, Long userId) {
        Query query = new Query().with(Sort.by(Sort.Order.desc("created"))).limit(size).skip((page - 1)*size);
        List<TimeLine> lineList = mongoTemplate.find(query, TimeLine.class,"quanzi_time_line_" + userId);
        long total = mongoTemplate.count(query, TimeLine.class, "quanzi_time_line_" + userId);
        ArrayList<Publish> publishes = new ArrayList<>();
        for (TimeLine line : lineList) {
            Publish publish = mongoTemplate.findById(line.getPublishId(), Publish.class);
            if (publish!=null){
                publishes.add(publish);
            }
        }
        return new PageResult(total,(long)size,(long)Math.ceil((total*1.0)/size),(long)page,publishes);
    }

    @Override
    public PageResult findByRecommend(int page, int size,Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId)).with(Sort.by(Sort.Order.desc("created"))).limit(size).skip((page - 1)*size);
        List<RecommendQuanzi> recommendUserList = mongoTemplate.find(query, RecommendQuanzi.class);
        long count = mongoTemplate.count(query, RecommendUser.class);
        ArrayList<Publish> publishArrayList = new ArrayList<>();
        for (RecommendQuanzi recommendQuanzi : recommendUserList) {
           if (recommendQuanzi.getPublishId()!=null){
               Publish publish = mongoTemplate.findById(recommendQuanzi.getPublishId(), Publish.class);
               if (publish!=null){
                   publishArrayList.add(publish);
               }
           }
        }
        return new PageResult(count,(long)size,(long)Math.ceil((count*1.0)/size),(long)page,publishArrayList);
    }

    @Override
    public PageResult findMyPublish(int page, int size, Long userId) {
        Query query = new Query().limit(size).skip((page - 1) * size).with(Sort.by(Sort.Order.desc("created")));
        List<Album> list = mongoTemplate.find(query, Album.class, "quanzi_album_" + userId);
        long total = mongoTemplate.count(query,Album.class, "quanzi_album_" + userId);
        ArrayList<Publish> list1 = new ArrayList<>();
        for (Album album : list) {
            if (album.getPublishId()!=null){
                Publish publish = mongoTemplate.findById(album.getPublishId(), Publish.class);
                if (publish!=null){
                    list1.add(publish);
                }
            }
        }
        return new PageResult(total,(long)size,(long)Math.ceil((total*1.0)/size),(long)page,list1);
    }

    @Override
    public long save(Comment comment) {
        updateCount(comment,1);
        long l = getCount(comment);
        comment.setId(new ObjectId());
        comment.setCreated(System.currentTimeMillis());
        mongoTemplate.save(comment);
        return l;
    }

    @Override
    public long delete(Comment comment) {
        Query query = new Query();
        query.addCriteria(Criteria.where("publishId").is(comment.getPublishId()).and("commentType").is(comment.getUserId()));
        mongoTemplate.remove(query,Comment.class);
        updateCount(comment,-1);
        long l = getCount(comment);
        return l;
    }

    @Override
    public Publish findById(String publishId) {
        Publish publish = mongoTemplate.findById(publishId, Publish.class);
        return publish;
    }

    @Override
    public PageResult findComment(int page, int size, String publishId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("publishId").is(new ObjectId(publishId)).and("commentType").is(2)).with(Sort.by(Sort.Order.desc("created"))).limit(size).skip((page-1)*size);
        List<Comment> list = mongoTemplate.find(query, Comment.class);
        long count = mongoTemplate.count(query, Comment.class);
        return new PageResult(count,(long)size,(long)Math.ceil((count*1.0)/size),(long)page,list);
    }

    @Override
    public PageResult findComment(int page, int size, Integer commentType, Long publishUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("publishUserId").is(publishUserId).and("commentType").is(commentType)).limit(size).skip((page-1)*size);
        List<Comment> list = mongoTemplate.find(query, Comment.class);
        long total = mongoTemplate.count(query, Comment.class);
        return new PageResult(total,(long)size,(long)Math.ceil((total*1.0)/size),(long)page,list);
    }

    @Override
    public Comment findById(Integer type, String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id).and("pubType").is(type));
        Comment comment = mongoTemplate.findOne(query, Comment.class);
        return comment;
    }

    //更新动态的点赞数或喜欢数以及发布数
    private void updateCount(Comment comment,int value){
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(comment.getId()));
        Update update = new Update();
        update.inc(comment.getCol(),value);
        Class aClass = Publish.class;
        if (comment.getPubType()==3){
            aClass=Comment.class;
        }
        mongoTemplate.updateFirst(query,update,aClass);
    }
    private long getCount(Comment comment){
        Query query = new Query(Criteria.where("id").is(comment.getPublishId()));
        if(comment.getPubType() == 1){
            Publish publish = mongoTemplate.findOne(query, Publish.class);
            if(comment.getCommentType() == 1){// //评论类型，1-点赞，2-评论，3-喜欢
                return (long)publish.getLikeCount();
            }
            if(comment.getCommentType() == 2){// //评论类型，1-点赞，2-评论，3-喜欢
                return (long)publish.getCommentCount();
            }
            if(comment.getCommentType() == 3){// //评论类型，1-点赞，2-评论，3-喜欢
                return (long)publish.getLoveCount();
            }
        }
        if(comment.getPubType() == 3){
            Comment cm = mongoTemplate.findOne(query, Comment.class);
            return (long)cm.getLikeCount();
        }
        return 99l;
    }
}
