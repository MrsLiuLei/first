package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.NearUserVo;
import com.tanhua.domain.mongo.UserLocation;
import com.tanhua.domain.vo.UserLocationVo;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;


@Service
public class UserLocationApiImpl implements UserLocationApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void add(Double latitude, Double longitude, String address, Long userId) {
        long l = System.currentTimeMillis();
        Query query = new Query(Criteria.where("userId").is(userId));
        if (!mongoTemplate.exists(query, UserLocation.class)){
            UserLocation userLocation = new UserLocation();
            userLocation.setAddress(address);
            userLocation.setCreated(l);
            userLocation.setId(ObjectId.get());
            userLocation.setUserId(userId);
            userLocation.setLastUpdated(l);
            userLocation.setUpdated(l);
            userLocation.setLocation(new GeoJsonPoint(latitude,longitude));
            mongoTemplate.save(userLocation);
        }else {
            Update update = new Update();
            update.set("lastUpdated",l);
            update.set("updated",l);
            update.set("location",new GeoJsonPoint(latitude, longitude));
            mongoTemplate.updateFirst(query,update,UserLocation.class);
        }
    }

    @Override
    public List<UserLocationVo> findNear(Long userId, Long miles) {
        Query query = new Query(Criteria.where("userId").is(userId));
        UserLocation location = mongoTemplate.findOne(query, UserLocation.class);
        GeoJsonPoint point = location.getLocation();
        Distance distance = new Distance(miles / 1000, Metrics.KILOMETERS);
        Circle circle = new Circle(point, distance);
        Query query1 = new Query(Criteria.where("location").withinSphere(circle));
        List<UserLocation> list = mongoTemplate.find(query1, UserLocation.class);
        return UserLocationVo.formatToList(list);
    }
}
