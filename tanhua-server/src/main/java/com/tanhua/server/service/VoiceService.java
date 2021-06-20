package com.tanhua.server.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Voice;
import com.tanhua.domain.vo.VoiceVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VoiceApi;
import com.tanhua.server.UserHolder;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class VoiceService {
    @Autowired
    private FastFileStorageClient client;
    @Autowired
    private FdfsWebServer webServer;
    @Autowired
    private RedisTemplate redisTemplate;
    @Reference
    private UserInfoApi userInfoApi;
    @Reference
    private VoiceApi voiceApi;
    public void addVoice(MultipartFile soundFile) throws IOException {
        try {
            Long userId = UserHolder.getUserId();
            String key = "sendMessage" + userId;
            redisTemplate.opsForValue().set(key,"3");
            String filename = soundFile.getOriginalFilename();
            String substring = filename.substring(filename.lastIndexOf(".") + 1);
            StorePath path = client.uploadFile(soundFile.getInputStream(), soundFile.getSize(), substring, null);
            Voice voice = new Voice();
            voice.setId(ObjectId.get());
            voice.setCreated(System.currentTimeMillis());
            voice.setUserId(userId);
            voice.setVoiceUrl(webServer.getWebServerUrl()+path.getFullPath());
            System.out.println(webServer.getWebServerUrl() + path.getFullPath());
            voiceApi.add(voice);
        } finally {
            String message="你还有3次机会";
            throw new TanHuaException(message);
        }
    }
    public VoiceVo find(){
        Date date = new Date();
        long time = date.getTime();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        date.setDate(date.getDate()+1);
        long time1 = date.getTime();
        List<Voice> list = voiceApi.find(UserHolder.getUserId());
        Voice voice = list.get(RandomUtils.nextInt(0, list.size()-1));
        VoiceVo voiceVo = new VoiceVo();
        Long userId = voice.getUserId();
        UserInfo userInfo = userInfoApi.findById(userId);
        if (userInfo!=null){
            BeanUtils.copyProperties(userInfo,voiceVo);
        }
        voiceVo.setSoundUrl(voice.getVoiceUrl());
        Long times =(Long) redisTemplate.opsForValue().get("times");
        if (times==null){
            long l = time1 - time;
            redisTemplate.opsForValue().set("times",10L, l, TimeUnit.MILLISECONDS);
            voiceVo.setRemainingTimes(times);
        }else {
            voiceVo.setRemainingTimes(times);
            if (times<=0){
                redisTemplate.opsForValue().set("times",0l);
            }else {
                redisTemplate.opsForValue().set("times",times-1);
            }
        }

        return voiceVo;
    }
}
