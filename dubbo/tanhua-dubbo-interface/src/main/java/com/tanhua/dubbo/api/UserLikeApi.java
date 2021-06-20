package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.UserLike;
import com.tanhua.domain.vo.PageResult;

public interface UserLikeApi {
    Long countLikeEach(Long userId);
    Long countLike(Long userId);
    Long countFens(Long userId);
    PageResult findLikeEach(Long userId,int page,int size);
    PageResult findFens(Long userId,int page,int size);
    PageResult findLike(Long userId,int page,int size);
    PageResult findVisitor(Long userId,int page,int size);
    void delete(Long userId,Long likeUserId);
    void add(UserLike userLike);
}
