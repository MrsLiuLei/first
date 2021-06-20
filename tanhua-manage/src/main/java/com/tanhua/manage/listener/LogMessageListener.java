package com.tanhua.manage.listener;

import com.alibaba.fastjson.JSON;
import com.tanhua.manage.domain.Log;
import com.tanhua.manage.service.LogService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RocketMQMessageListener(topic = "tanhua-log",consumerGroup = "tanhua-log-consumer")
public class LogMessageListener implements RocketMQListener<String > {
    @Autowired
    private LogService logService;
    @Override
    public void onMessage(String s) {
        Map map = JSON.parseObject(s, Map.class);
        Object o = map.get("userId");
        String s1 = o.toString();
        String type = (String) map.get("type");
        String date = (String) map.get("date");
        Log log = new Log();
        log.setUser_id(Long.valueOf(s1));
        log.setLog_time(date);
        log.setType(type);
        logService.save(log);
    }
}
