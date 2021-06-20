package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.Voice;
import com.tanhua.domain.vo.PageResult;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


@Service
public class VoiceApiImpl implements VoiceApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void add(Voice voice) {
        mongoTemplate.save(voice);
    }

    @Override
    public List<Voice> find(Long userId) {
        Query query = new Query(Criteria.where("userId").ne(userId));
        query.limit(10).with(Sort.by(Sort.Order.desc("created")));
        List<Voice> list = mongoTemplate.find(query, Voice.class);
        return list;
    }
}
