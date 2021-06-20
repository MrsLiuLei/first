package com.tanhua.server.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.VideoVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.server.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService {
    @Reference
    private VideoApi videoApi;
    @Reference
    private UserInfoApi userInfoApi;
    @Autowired
    private FastFileStorageClient client;
    @Autowired
    private FdfsWebServer webServer;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OssTemplate ossTemplate;
    //发布小视频
    public void add(MultipartFile videoThumbnail, MultipartFile videoFile ) throws IOException {
        Long userId = UserHolder.getUserId();
        String picUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
        String originalFilename = videoFile.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        StorePath path = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), substring, null);
        Video video = new Video();
        video.setPicUrl(picUrl);
        video.setVideoUrl(webServer.getWebServerUrl()+path.getFullPath());
        video.setText("百鸟朝凤");
        video.setUserId(userId);
        videoApi.save(video);
    }
    public ResponseEntity findPage(int page,int size){
        PageResult result = videoApi.findPage(page, size);
        List<Video> items = result.getItems();
        ArrayList<VideoVo> videoVos = new ArrayList<>();
        if(items!=null){
            for (Video item : items) {
                Long userId = item.getUserId();
                UserInfo userInfo = userInfoApi.findById(userId);
                VideoVo videoVo = new VideoVo();
                if (userInfo!=null){
                    BeanUtils.copyProperties(userInfo,videoVo);
                }
                BeanUtils.copyProperties(item,videoVo);
                videoVo.setCover(item.getPicUrl());
                videoVo.setId(item.getId().toHexString());
                if (StringUtils.isEmpty(item.getText())){
                    videoVo.setSignature("默认签名");
                }else {
                    videoVo.setSignature(item.getText());
                }
                String key = "video_follow_"+UserHolder.getUserId()+"_"+item.getUserId();
                if(redisTemplate.hasKey(key)) {
                   videoVo.setHasFocus(1); //TODO 是否关注
                }else{
                    videoVo.setHasFocus(0); //TODO 是否关注
                }
                videoVo.setHasLiked(0);
                videoVos.add(videoVo);
            }
        }
        result.setItems(videoVos);
        //System.out.println(result);
        return ResponseEntity.ok(result);
    }
    public void add1(Long followUserId){
        FollowUser followUser = new FollowUser();
        followUser.setUserId(UserHolder.getUserId());
        followUser.setFollowUserId(followUserId);
        videoApi.add(followUser);
        // 记录redis，表示当前用户关系了这个作者
        String key = "video_follow_" + UserHolder.getUserId()+ "_" + followUserId;
        redisTemplate.opsForValue().set(key,"1");
    }
    public void delete(Long followUserId){
        FollowUser followUser = new FollowUser();
        followUser.setUserId(UserHolder.getUserId());
        followUser.setFollowUserId(followUserId);
        videoApi.delete(followUser);
        String key = "video_follow_" + UserHolder.getUserId()+ "_" + followUserId;
        redisTemplate.delete(key);
    }
}
