package com.tanhua.dubbo.api;

import com.tanhua.domain.vo.PageResult;

public interface FriendApi {
    void add(Long userId,Long friendId);
    PageResult findPage(Long userId,Integer page,Integer size,String keyword);
}
