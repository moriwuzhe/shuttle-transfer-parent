package org.moriwuzhe.shuttle.transfer.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: xjp
 * @Date: 2021-05-07 0:20
 * @Version: 1.0
 */
@lombok.Getter
@lombok.AllArgsConstructor
public enum ShuttleContentTypeEnum {
    /**
     * 文本
     */
    TEXT("text"),
    /**
     * xml
     */
    XML("xml"),
    /**
     * json
     */
    JSON("json"),
    ;

    String content;

    private final static Map<String, ShuttleContentTypeEnum> maps =
            Arrays.stream(values()).collect(Collectors.toMap(ShuttleContentTypeEnum::getContent, e -> e));

    public static ShuttleContentTypeEnum getInstance(String content) {
        return maps.get(content);
    }
}
