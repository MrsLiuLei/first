package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Setting;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.PageResult;

public interface SettingsApi {
    Setting findByUserId(Long userId);
    Question findQuestionByUserId(Long userId);
    void add(Setting setting);
    void update(Setting setting);
    void aaQuestion(Question question);
    void updateQuestion(Question question);
    PageResult<UserInfo> findBlackList(int page,int pageSize,Long id);
    void deleteBlack(Long user_id,Long black_user_id);
}
