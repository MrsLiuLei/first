package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceVo implements Serializable {
    private Long id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String soundUrl;
    private Long remainingTimes;
}
