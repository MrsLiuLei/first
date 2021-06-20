package com.tanhua.manage.job;

import com.tanhua.manage.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AnalysisJob {
    @Autowired
    private AnalysisService service;
    @Scheduled(cron = "0/30 * * * * ?")
    public void analysis(){
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("开始进行统计时间是："+now);
        service.analysis();
        System.out.println("完成数据统计");
    }
}
