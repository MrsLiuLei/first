package com.tanhua.dubbo.api;
import com.tanhua.domain.mongo.NearUserVo;
import com.tanhua.domain.vo.UserLocationVo;

import java.util.List;

public interface UserLocationApi {
    void add(Double latitude,Double longitude,String address,Long userId);
    List<UserLocationVo> findNear(Long userId, Long miles);
}
