package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;

public interface RecommendUserApi {
    RecommendUser findMaxScore(Long toUserId);
    PageResult<RecommendUser> findPage(int page,int size,Long toUserId);
    Double findScore(Long userId,Long toUserId);

}
