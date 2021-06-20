package com.tanhua.domain.mongo;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(collection = "video")
public class Video implements Serializable {
    private ObjectId id;
    private Long vid;
    private Long userId;

    private String text; //文字
    private String picUrl;
    private String videoUrl;
    private Long created;

    private Integer likeCount=0; //点赞数
    private Integer commentCount=0; //评论数
    private Integer loveCount=0; //喜欢数
}
