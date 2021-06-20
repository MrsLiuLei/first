package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.PageResult;

import java.util.List;

public interface UserInfoApi {
    void save(UserInfo userInfo);
    void update(UserInfo userInfo);
    UserInfo findById(Long userId);
    IPage find(long page, long size);
    List<UserInfo> findByCid(String cid);
}
