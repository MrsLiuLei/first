package com.tanhua.server.controller;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.domain.vo.VisitorVo;
import com.tanhua.server.service.QuanziService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/movements")
public class QuanziController {
    @Autowired
    private QuanziService quanziService;
   //发布动态
    @PostMapping
    public ResponseEntity postMoment(PublishVo publishVo, MultipartFile[] imageContent) throws IOException {
        quanziService.save(publishVo, imageContent);
        return ResponseEntity.ok(null);
    }
    //获得朋友动态
    @GetMapping
    public ResponseEntity findFriendPublish(@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int pagesize){
        return quanziService.findFriendPublish(page,pagesize);
    }
    @RequestMapping (value = "/recommend",method = RequestMethod.GET)
    //获得推荐动态
    public ResponseEntity queryRecommendPublishList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize){
        page=page<1?1:page;
       return quanziService.findRecommend(page,pagesize);
    }
    @GetMapping("/all")
    public ResponseEntity findMyPublish(@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int pagesize,Long userId){
        return quanziService.findMyPublish(page,pagesize,userId);
    }
    //点赞
    @GetMapping("/{id}/like")
    public ResponseEntity<Long> like(@PathVariable("id") String publishId){
        Long total = quanziService.like(publishId);
        return ResponseEntity.ok(total);
    }

    /**
     * 取消点赞
     * @param publishId
     * @return
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity<Long> unlike(@PathVariable("id") String publishId){
        Long total =quanziService.unlike(publishId);
        return ResponseEntity.ok(total);
    }@GetMapping("/{id}/love")
    public ResponseEntity<Long> love(@PathVariable("id") String publishId){
        Long total = quanziService.love(publishId);
        return ResponseEntity.ok(total);
    }

    /**
     * 取消喜欢
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity<Long> unlove(@PathVariable("id") String publishId){
        Long total = quanziService.unlove(publishId);
        return ResponseEntity.ok(total);
    }
    //查看动态详情
    @GetMapping("/{id}")
    public ResponseEntity findPublishById(@PathVariable("id") String publishId){
        return quanziService.findPublishById(publishId);
    }
    @GetMapping("/visitors")
    public ResponseEntity queryVisitors(){
        List<VisitorVo> visitorVoList = quanziService.queryVisitors();
        return ResponseEntity.ok(visitorVoList);
    }
}

