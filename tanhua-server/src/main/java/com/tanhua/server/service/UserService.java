package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.commons.templates.FaceTemplate;
import com.tanhua.commons.templates.HuanXinTemplate;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.commons.templates.SmsTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.*;
import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.UserLikeApi;
import com.tanhua.server.UserHolder;
import com.tanhua.server.utils.GetAgeUtil;
import com.tanhua.server.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.ibatis.parsing.TokenHandler;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {
    @Autowired
    private JwtUtils jwtUtils;
    @Reference
    private UserLikeApi userLikeApi;
    @Reference
    private FriendApi friendApi;
    @Reference
    private UserApi userApi;
    @Reference
    private UserInfoApi userInfoApi;
    @Autowired
    private HuanXinTemplate huanXinTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private SmsTemplate smsTemplate;
    @Autowired
    private OssTemplate ossTemplate;
    @Autowired
    private FaceTemplate faceTemplate;
    @Value("${tanhua.redisValidateCodeKeyPrefix}")
    private String redisValidateCodeKeyPrefix;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public ResponseEntity findByMobile(String mobile) {
        User user = userApi.findByMobile(mobile);
        return ResponseEntity.ok(user);
    }

    /**
     * 新增用户
     *
     * @param mobile
     * @return
     */
    public ResponseEntity saveUser(String mobile, String password) {
        User user = new User();
        user.setMobile(mobile);
        user.setPassword(password);
        userApi.save(user);
        return ResponseEntity.ok(user.getId());
    }

    public void updateUser(String phone,String token) {
        userApi.updatePhone(phone, UserHolder.getUserId());
        redisTemplate.delete("TOKEN_"+token);
    }

    public void sendValidateCode(String phone) {
        //1.redis中存入验证码的key
        String key = redisValidateCodeKeyPrefix + phone;
        //2.redis中的验证码
        String codeInRedis = redisTemplate.opsForValue().get(key);
        //3.如果值存在 提示上一次发送的验证码还未失效
        if (StringUtils.isNotEmpty(codeInRedis)) {
            throw new TanHuaException(ErrorResult.duplicate());
        }
        //4.生成验证码
        String validateCode = RandomStringUtils.randomNumeric(6);
        //5.发送验证码
        log.debug("发送验证码：{}，{}", phone, validateCode);
        Map<String, String> smsRs = smsTemplate.sendValidateCode(phone, validateCode);
        if (smsRs != null) {
            throw new TanHuaException(ErrorResult.fail());
        }
        //6.将验证码存入redis 有效时间5分钟
        log.info("将验证码存入redis");
        redisTemplate.opsForValue().set(key, validateCode, 5, TimeUnit.MINUTES);
    }
    public Map<String, Boolean> loginVerification2(String phone,String verificationCode){
        Map<String, Object> map1 = loginVerification(phone, verificationCode);
        HashMap<String, Boolean> map = new HashMap<>();
        if (map1==null){
           map.put("verification",false);
        }else {
            map.put("verification",true);
        }
        return map;
    }

    public Map<String, Object> loginVerification(String phone, String verificationCode) {
        String key = redisValidateCodeKeyPrefix + phone;
        String codeInRedis = redisTemplate.opsForValue().get(key);
        String type="0101";
        if (StringUtils.isEmpty(codeInRedis)) {
            throw new TanHuaException(ErrorResult.loginError());
        }
        if (!codeInRedis.equals(verificationCode)) {
            throw new TanHuaException(ErrorResult.validateCodeError());
        }
        User user = userApi.findByMobile(phone);
        boolean isNew = false;
        if (user == null) {
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex(phone.substring(phone.length() - 6)));
            Long userId = userApi.save(user);
            user.setId(userId);
            huanXinTemplate.register(user.getId());
            type="0102";
            isNew = true;
        }
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("userId",user.getId());
        map1.put("type",type);
        map1.put("date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        rocketMQTemplate.convertAndSend("tanhua-log",map1);
        String token = jwtUtils.createJwt(phone, user.getId());
        String string = JSON.toJSONString(user);
        redisTemplate.opsForValue().set("TOKEN_" + token, string, 1, TimeUnit.DAYS);
        HashMap<String, Object> map = new HashMap<>();
        map.put("isNew", isNew);
        map.put("token", token);
        redisTemplate.delete(key);
        return map;
    }

    public User getUserByToken(String token) {
        String key = "TOKEN_" + token;
        String userJsonStr = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(userJsonStr)) {
            return null;
        }
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
        User user = JSON.parseObject(userJsonStr, User.class);
        return user;
    }

    public void saveUserInfo(UserInfo userInfo) {
        User user = UserHolder.getUser();
        if (user == null) {
            throw new TanHuaException("登陆超时，请重新登陆");
        }
        userInfo.setId(user.getId());
        userInfoApi.save(userInfo);
    }

    public void updateUserAvatar(MultipartFile headPhoto) {
        User user = UserHolder.getUser();
        ;
        if (user == null) {
            throw new TanHuaException("登陆超时，请重新登陆");
        }
        try {
            String filename = headPhoto.getOriginalFilename();
            byte[] bytes = headPhoto.getBytes();
            if (!faceTemplate.detect(bytes)) {
                throw new TanHuaException("没有检测到人脸，请重新上传");
            }
            String s = ossTemplate.upload(filename, headPhoto.getInputStream());
            UserInfo info = userInfoApi.findById(user.getId());
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setAvatar(s);
            userInfoApi.update(userInfo);
            ossTemplate.deleteFile(info.getAvatar());
        } catch (IOException e) {
            //e.printStackTrace();
            log.error("上传头像失败", e);
            throw new TanHuaException("上传头像失败，请稍后重试");
        }
    }

    public void uodateUserInfo(UserInfoVo vo) {
        User user = UserHolder.getUser();
        ;
        if (user == null) {
            throw new TanHuaException("登陆超时，请重新登陆");
        }
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(vo, userInfo);
        userInfo.setAge(GetAgeUtil.getAge(userInfo.getBirthday()));
        userInfo.setId(user.getId());
        userInfoApi.update(userInfo);
    }

    public UserInfoVo findUserInfoById(Long userId) {
        UserInfo userInfo = userInfoApi.findById(userId);
        UserInfoVo info = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, info);
        if (userInfo.getAge() != null) {
            info.setAge(String.valueOf(userInfo.getAge().intValue()));
        }
        return info;
    }
    public ResponseEntity count(){
        Long userId = UserHolder.getUserId();
        Long fens = userLikeApi.countFens(userId);
        Long like = userLikeApi.countLike(userId);
        Long likeEach = userLikeApi.countLikeEach(userId);
        CountVo countVo = new CountVo(likeEach,like,fens);
        return ResponseEntity.ok(countVo);
    }
    public ResponseEntity findUserLike(int page,int pagesize,int type){
        Long userId = UserHolder.getUserId();
        PageResult pageResult = new PageResult<>();
        switch (type){
            case 1:
                pageResult = userLikeApi.findLikeEach(userId,page,pagesize);
                break;
            case 2:
                pageResult = userLikeApi.findLike(userId,page,pagesize);
                break;
            case 3:
                pageResult = userLikeApi.findFens(userId,page,pagesize);
                break;
            case 4:
                pageResult = userLikeApi.findVisitor(userId,page,pagesize);
                break;
            default: break;
        }
        List<UserInfo> items = pageResult.getItems();
        ArrayList<FriendVo> friendVos = new ArrayList<>();
        if (items.size()>0&&items!=null){
            for (UserInfo item : items) {
                FriendVo friendVo = new FriendVo();
                BeanUtils.copyProperties(item,friendVo);
                friendVo.setMatchRate(RandomUtils.nextInt(60,100));
                friendVos.add(friendVo);
            }
        }
        pageResult.setItems(friendVos);
        return ResponseEntity.ok(pageResult);
    }
    public ResponseEntity fansLike(Long likeUserId){
        userLikeApi.delete(likeUserId,UserHolder.getUserId());
        friendApi.add(UserHolder.getUserId(),likeUserId);
        huanXinTemplate.makeFriends(UserHolder.getUserId(),likeUserId);
        return ResponseEntity.ok(null);
    }
    public ResponseEntity findPage(){
        IPage iPage = userInfoApi.find(1L, 10L);
        List<UserInfo> list = iPage.getRecords();
        ArrayList<CardsVo> cardsVos = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (UserInfo userInfo : list) {
                CardsVo cardsVo = new CardsVo();
                BeanUtils.copyProperties(userInfo,cardsVo);
                cardsVo.setTags(StringUtils.split(userInfo.getTags(),","));
                cardsVos.add(cardsVo);
            }
        }
        return ResponseEntity.ok(cardsVos);
    }
}
