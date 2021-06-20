package com.tanhua.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.manage.domain.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LogMapper extends BaseMapper<Log> {
    @Select("SELECT COUNT(DISTINCT user_id) FROM tb_log WHERE log_time=#{logTime} AND TYPE=#{type}")
    Integer findNumByType(@Param("logTime") String today, @Param("type") String type);

    //根据日期统计总数
    @Select("SELECT COUNT(DISTINCT  user_id) FROM tb_log WHERE log_time=#{logTime}")
    Integer findNumsByDate(String todayStr);

    //根据日期，统计次日留存
    @Select("SELECT  COUNT(DISTINCT  user_id) FROM tb_log WHERE log_time=#{today} AND user_id IN (\n" +
            "    SELECT user_id FROM tb_log WHERE log_time=#{yestoday} AND TYPE='0102'\n" +
            ")")
    Integer findRetention1d(@Param("today") String todayStr, @Param("yestoday") String yestodayStr);
}
