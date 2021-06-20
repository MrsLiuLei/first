package com.tanhua.domain.mongo;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(collection = "voice")
public class Voice implements Serializable {
    private ObjectId id;
    private Long userId;
    private String voiceUrl;
    private Long created;

}
