package com.tanhua.manage.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Log {
    private Long id;
    private Long user_id;
    /**
     * 操作类型,
     * 0101为登录，0102为注册，0201为发动态，0202为浏览动态，0203为动态点赞，0204为动态喜欢，0205为评论，0206为动态取消点赞，0207为动态取消喜欢，0301为发小视频，0302为小视频点赞，0303为小视频取消点赞，0304为小视频评论
     */
    private String type;
    private String log_time;
    private String place;
    private String equipment;
}
