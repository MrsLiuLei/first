package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class RecommendUserApiImpl implements RecommendUserApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public RecommendUser findMaxScore(Long toUserId) {
        Query query = Query.query(Criteria.where("toUserId").is(toUserId)).with(Sort.by(Sort.Order.desc("score"))).limit(1);
        return mongoTemplate.findOne(query,RecommendUser.class);
    }

    @Override
    public PageResult<RecommendUser> findPage(int page, int size, Long toUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("toUserId").is(toUserId));
        long count = mongoTemplate.count(query, RecommendUser.class);
        query.with(PageRequest.of(page-1,size,Sort.by(Sort.Order.desc("score"))));
        List<RecommendUser> recommendUsers = mongoTemplate.find(query, RecommendUser.class);
        PageResult<RecommendUser> pageResult = new PageResult<>();
        pageResult.setCounts(count);
        pageResult.setPagesize((long)size);
        pageResult.setPage((long)page);
        pageResult.setPages((long)Math.ceil((count*1.0)/size));
        pageResult.setItems(recommendUsers);
        return pageResult;
    }

    @Override
    public Double findScore(Long userId, Long toUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("toUserId").is(toUserId).and("userId").is(userId));
        query.with(Sort.by(Sort.Order.desc("date")));
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        if (recommendUser!=null){
            return recommendUser.getScore();
        }
        return null;
    }

}
