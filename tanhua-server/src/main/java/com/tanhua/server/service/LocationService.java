package com.tanhua.server.service;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.NearUserVo;
import com.tanhua.domain.vo.UserLocationVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.UserLocationApi;
import com.tanhua.server.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LocationService {
    @Reference
    private UserInfoApi userInfoApi;
    @Reference
    private UserLocationApi userLocationApi;
    public void addLocation(Map<String,Object> map){
        Double latitude = (Double)map.get("latitude");
        Double longitude = (Double)map.get("longitude");
        String address = (String) map.get("addrStr");
        userLocationApi.add(latitude,longitude,address, UserHolder.getUserId());
    }
    public List<NearUserVo> searchNearBy(String gender, String distance){
        Long userId = UserHolder.getUserId();
        List<UserLocationVo> near = userLocationApi.findNear(userId, Long.valueOf(distance));
        ArrayList<NearUserVo> nearUserVos = new ArrayList<>();
        for (UserLocationVo userLocationVo : near) {
            UserInfo userInfo = userInfoApi.findById(userLocationVo.getUserId());
            if (userInfo.getId().toString().equals(userId.toString())){
                continue;
            }
            if (gender!=null&&!userInfo.getGender().equals(gender)){
                continue;
            }
            NearUserVo nearUserVo = new NearUserVo();
            nearUserVo.setUserId(userInfo.getId());
            nearUserVo.setAvatar(userInfo.getAvatar());
            nearUserVo.setNickname(userInfo.getNickname());
            nearUserVos.add(nearUserVo);
        }
        return nearUserVos;
    }
}
