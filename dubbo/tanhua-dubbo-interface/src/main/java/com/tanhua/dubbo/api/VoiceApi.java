package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.Voice;
import com.tanhua.domain.vo.PageResult;

import java.util.List;

public interface VoiceApi {
    void add(Voice voice);
    List<Voice> find(Long userId);
}
