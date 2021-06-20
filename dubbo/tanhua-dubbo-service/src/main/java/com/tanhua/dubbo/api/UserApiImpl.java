package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.User;
import com.tanhua.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
@Service
public class UserApiImpl implements UserApi{
    @Autowired
    private UserMapper userMapper;

    @Override
    public Long save(User user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public User findByMobile(String mobile) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("mobile",mobile);
        return userMapper.selectOne(queryWrapper);
    }
    @Override
    public void updatePhone(String phone, Long id) {
        User user = new User();
        user.setId(id);
        user.setMobile(phone);
        userMapper.updateById(user);
    }

}
