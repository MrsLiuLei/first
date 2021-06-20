package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.Visitor;

import java.util.List;

public interface VisitorApi {
    /**
     * 根据上次访问时间与当前用户id 查询前5条访客列表记录
     * @param time
     * @param userId
     * @return
     */
    List<Visitor> queryVisitors(String time, Long userId);

    /**
     * 根据当前用户id 查询前5条访客列表记录
     * @param userId
     * @return
     */
    List<Visitor> queryVisitors(Long userId);

    /**
     * 保存访客记录
     */
    void save(Visitor visitor);
}
