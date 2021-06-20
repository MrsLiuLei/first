package liu;

import com.tanhua.domain.mongo.User;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongoApplication.class)
public class Test1 {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Test
    public void testAdd(){
        for (int i=0;i<50;i++){
            User user = new User(ObjectId.get(), "liu" + i, i);
            mongoTemplate.save(user);
        }
    }
    @Test
    public void testFindAll(){
        Query query = Query.query(Criteria.where("age").lt(20));
        List<User> list = mongoTemplate.find(query, User.class);
        for (User user : list) {
            System.out.println(user);
        }
    }
    @Test
    public void testFingPage(){
        int page=1;
        int size=5;
        Query query = new Query().limit(size).skip((page - 1) * size);
        List<User> users = mongoTemplate.find(query, User.class);
        for (User user : users) {
            System.out.println(user);
        }
        long count = mongoTemplate.count(query, User.class);
    }
    @Test
    public void testDelete(){
        Query query = Query.query(Criteria.where("id").lte(100));
        mongoTemplate.remove(query,User.class);
    }
    @Test
    public void testUpdate(){

    }
}
