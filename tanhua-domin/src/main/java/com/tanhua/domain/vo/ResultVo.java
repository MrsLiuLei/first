package com.tanhua.domain.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class ResultVo {
    private String conclusion;
    private String cover;
    private List<ResultVo1> dimensions;
    private List<SexVo> similarYou;
}
