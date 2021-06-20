package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;

public interface VideoApi {
    void save(Video video);
    PageResult findPage(int page,int size);
    void add(FollowUser followUser);
    void delete(FollowUser followUser);
    PageResult findAll(int page,int size,Long uid);
}
