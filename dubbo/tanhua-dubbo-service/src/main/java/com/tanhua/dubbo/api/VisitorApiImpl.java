package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.Visitor;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
@Service
public class VisitorApiImpl implements VisitorApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public List<Visitor> queryVisitors(String time, Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId).and("date").gt(time)).limit(5);
        List<Visitor> list = mongoTemplate.find(query, Visitor.class);
        return list;
    }

    @Override
    public List<Visitor> queryVisitors(Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId)).with(Sort.by(Sort.Order.desc("date"))).limit(5);
        List<Visitor> list = mongoTemplate.find(query, Visitor.class);
        return list;
    }

    @Override
    public void save(Visitor visitor) {
        visitor.setId(ObjectId.get());
        visitor.setDate(System.currentTimeMillis());
        mongoTemplate.save(visitor);
    }
}
