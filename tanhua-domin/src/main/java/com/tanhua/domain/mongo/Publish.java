package com.tanhua.domain.mongo;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
@Data
@Document(collection = "quanzi_publish")
public class Publish implements Serializable {
    private ObjectId id;
    private Long pid;
    private Long userId;
    private String textContent;
    private List<String> medias;
    private Integer seeType; // 谁可以看，1-公开，2-私密，3-部分可见，4-不给谁看

    private String longitude; //经度
    private String latitude; //纬度
    private String locationName; //位置名称
    private Long created; //发布时间
    private Integer state=0;// 状态0：待审核，1：已审核，2：已驳回

    private Integer likeCount=0; //点赞数
    private Integer commentCount=0; //评论数
    private Integer loveCount=0; //喜欢数
}
