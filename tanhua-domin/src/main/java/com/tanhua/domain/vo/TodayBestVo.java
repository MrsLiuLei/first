package com.tanhua.domain.vo;

import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

@Data
public class TodayBestVo {
    private Long id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String[] tags;
    private Integer fateValue;
}
