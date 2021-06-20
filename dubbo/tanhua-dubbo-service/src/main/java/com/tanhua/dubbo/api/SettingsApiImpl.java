package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.*;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.mapper.BlackListMapper;
import com.tanhua.dubbo.mapper.QuestionMapper;
import com.tanhua.dubbo.mapper.SettingsMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
@Service
public class SettingsApiImpl implements SettingsApi{
    @Autowired
    private SettingsMapper settingsMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private BlackListMapper blackListMapper;
    @Override
    public Setting findByUserId(Long userId) {
        QueryWrapper<Setting> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        Setting setting = settingsMapper.selectOne(wrapper);
        return setting;
    }

    @Override
    public Question findQuestionByUserId(Long userId) {
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        Question question = questionMapper.selectOne(wrapper);
        return question;
    }

    @Override
    public void add(Setting setting) {
        settingsMapper.insert(setting);
    }

    @Override
    public void update(Setting setting) {
        settingsMapper.updateById(setting);
    }

    @Override
    public void aaQuestion(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public void updateQuestion(Question question) {
        questionMapper.updateById(question);
    }

    @Override
    public PageResult<UserInfo> findBlackList(int page, int pageSize, Long id) {
        Page page1 = new Page(page, pageSize);
        IPage iPage = blackListMapper.findBlackList(page1, id);
        PageResult<UserInfo> pageResult = new PageResult<>(iPage.getTotal(),iPage.getSize(),iPage.getPages(),iPage.getCurrent(),iPage.getRecords());
        return pageResult;
    }

    @Override
    public void deleteBlack(Long user_id, Long black_user_id) {
        QueryWrapper<BlackList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user_id)
                .eq("black_user_id", black_user_id);
        blackListMapper.delete(queryWrapper);
    }
}
