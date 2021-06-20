package com.tanhua.dubbo.api;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.domain.mongo.Visitor;
import com.tanhua.domain.vo.PageResult;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserLikeApiImpl implements UserLikeApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserInfoApi userInfoApi;
    @Override
    public Long countLikeEach(Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        long count = mongoTemplate.count(query, Friend.class);
        return count;
    }

    @Override
    public Long countLike(Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        long count = mongoTemplate.count(query, UserLike.class);
        return count;
    }

    @Override
    public Long countFens(Long userId) {
        Query query = new Query(Criteria.where("likeUserId").is(userId));
        long count = mongoTemplate.count(query, UserLike.class);
        return count;
    }

    @Override
    public PageResult findLikeEach(Long userId, int page, int size) {
        Query query = new Query(Criteria.where("userId").is(userId)).limit(size).skip((page-1)*size);
        long count = mongoTemplate.count(query, Friend.class);
        List<Friend> list = mongoTemplate.find(query, Friend.class);
        ArrayList<UserInfo> list1 = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (Friend friend : list) {
                UserInfo userInfo = userInfoApi.findById(friend.getFriendId());
                if (userInfo!=null){
                    list1.add(userInfo);
                }
            }
            return new PageResult(count,(long)size,(long)Math.ceil((1.0*count)/size),(long)page,list1);
        }
        return null;
    }

    @Override
    public PageResult findFens(Long userId, int page, int size) {
        Query query = new Query(Criteria.where("likeUserId").is(userId)).limit(size).skip((page-1)*size);
        long count = mongoTemplate.count(query, UserLike.class);
        List<UserLike> list = mongoTemplate.find(query, UserLike.class);
        ArrayList<UserInfo> list1 = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (UserLike userLike : list) {
                UserInfo userInfo = userInfoApi.findById(userLike.getUserId());
                if (userInfo!=null){
                    list1.add(userInfo);
                }
            }
            return new PageResult(count,(long)size,(long)Math.ceil((1.0*count)/size),(long)page,list1);
        }
        return null;
    }

    @Override
    public PageResult findLike(Long userId, int page, int size) {
        Query query = new Query(Criteria.where("UserId").is(userId)).limit(size).skip((page-1)*size);
        long count = mongoTemplate.count(query, UserLike.class);
        List<UserLike> list = mongoTemplate.find(query, UserLike.class);
        ArrayList<UserInfo> list1 = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (UserLike friend : list) {
                UserInfo userInfo = userInfoApi.findById(friend.getLikeUserId());
                if (userInfo!=null){
                    list1.add(userInfo);
                }
            }
            return new PageResult(count,(long)size,(long)Math.ceil((1.0*count)/size),(long)page,list1);
        }
        return null;
    }

    @Override
    public PageResult findVisitor(Long userId, int page, int size) {
        Query query = new Query(Criteria.where("UserId").is(userId)).limit(size).skip((page-1)*size);
        long count = mongoTemplate.count(query, Visitor.class);
        List<Visitor> list = mongoTemplate.find(query, Visitor.class);
        ArrayList<UserInfo> list1 = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (Visitor friend : list) {
                UserInfo userInfo = userInfoApi.findById(friend.getVisitorUserId());
                if (userInfo!=null){
                    list1.add(userInfo);
                }
            }
            return new PageResult(count,(long)size,(long)Math.ceil((1.0*count)/size),(long)page,list1);
        }
        return null;
    }

    @Override
    public void delete(Long userId, Long likeUserId) {
        Query query = new Query(Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId));
        mongoTemplate.remove(query,UserLike.class);
    }

    @Override
    public void add(UserLike userLike) {

        mongoTemplate.save(userLike);
    }
}
