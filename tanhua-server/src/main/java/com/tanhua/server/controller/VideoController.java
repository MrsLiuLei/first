package com.tanhua.server.controller;


import com.tanhua.server.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/smallVideos")
public class VideoController {
    @Autowired
    private VideoService videoService;
    @PostMapping
    public ResponseEntity post(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        videoService.add(videoThumbnail,videoFile);
        return ResponseEntity.ok(null);
    }
    @GetMapping
    public ResponseEntity findPage(@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int pagesize){
        page=page<1?1:page;
        return videoService.findPage(page,pagesize);
    }
  /*  *
     * 关注视频的作者
     * @param userId
     * @return*/
    @PostMapping("/{id}/userFocus")
    public ResponseEntity followUser(@PathVariable("id") long userId){
        videoService.add1(userId);
        return ResponseEntity.ok(null);
    }
   /* *
     * 取消关注
     * @param userId
     * @return*/
    @PostMapping("/{id}/userUnFocus")
    public ResponseEntity unfollowUser(@PathVariable("id") long userId){
        videoService.delete(userId);
        return ResponseEntity.ok(null);
    }

}
