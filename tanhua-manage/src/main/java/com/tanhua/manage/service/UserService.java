package com.tanhua.manage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.MomentVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.VideoVo;
import com.tanhua.dubbo.api.QuanziApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    @Reference
    private QuanziApi quanziApi;
    @Reference
    private VideoApi videoApi;
    @Reference
    private UserInfoApi userInfoApi;
    public ResponseEntity findPage(long page,long size){
        IPage iPage = userInfoApi.find(page, size);
        PageResult<UserInfo> pageResult = new PageResult<>(iPage.getTotal(),size,iPage.getPages(),page,iPage.getRecords());
        return ResponseEntity.ok(pageResult);
    }
    public UserInfo findById(Long userId){
        return userInfoApi.findById(userId);
    }
    public ResponseEntity findAllVideos(int page,int size,long uid){
        PageResult pageResult = videoApi.findAll(page, size, uid);
        List<Video> items = pageResult.getItems();
        ArrayList<VideoVo> videoVos = new ArrayList<>();
        if (items!=null){
            for (Video item : items) {
                UserInfo info = userInfoApi.findById(item.getUserId());
                VideoVo vo = new VideoVo();
                BeanUtils.copyProperties(info,vo);
                BeanUtils.copyProperties(item,vo);
                vo.setCover(item.getPicUrl());
                vo.setId(item.getId().toHexString());
                vo.setSignature(item.getText());//签名
                videoVos.add(vo);
            }
        }
        pageResult.setItems(videoVos);
        return ResponseEntity.ok(pageResult);
    }
    public ResponseEntity findAllMovements(int page,int size,Long uid){
        PageResult pageResult = quanziApi.findMyPublish(page, size, uid);
        List<Publish> items = pageResult.getItems();
        ArrayList<MomentVo> momentVos = new ArrayList<>();
        if(items != null) {
            for (Publish item : items) {
                MomentVo vo = new MomentVo();
                UserInfo userInfo = userInfoApi.findById(item.getUserId());
                if(userInfo != null) {
                    BeanUtils.copyProperties(userInfo,vo);
                    if(userInfo.getTags() != null) {
                        vo.setTags(userInfo.getTags().split(","));
                    }
                }
                BeanUtils.copyProperties(item, vo);
                vo.setId(item.getId().toHexString());
                vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(item.getCreated())));
                vo.setImageContent(item.getMedias().toArray(new String[]{}));
                vo.setDistance("50米");
                momentVos.add(vo);
            }
        }
        pageResult.setItems(momentVos);
        return ResponseEntity.ok(pageResult);
    }
}
