package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserInfoApiImpl implements UserInfoApi{
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo findById(Long userId) {
        return userInfoMapper.selectById(userId);
    }

    @Override
    public IPage find(long page, long size) {
        IPage<UserInfo> userInfoIPage = userInfoMapper.selectPage(new Page<>(page, size), null);
        return userInfoIPage;
    }

    @Override
    public List<UserInfo> findByCid(String cid) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("cid",cid);
        List<UserInfo> list = userInfoMapper.selectList(wrapper);
        return list;
    }
}
