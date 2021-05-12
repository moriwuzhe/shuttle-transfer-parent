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
public enum ShuttleStatusEnum {
    /**
     * 错误
     */
    ERROR((byte) 0),
    /**
     * 非法参数
     */
    ILLEGAL((byte)1),
    /**
     * 成功
     */
    SUCCESS((byte)2),
    ;

    byte status;

    private final static Map<Byte, ShuttleStatusEnum> maps =
            Arrays.stream(values()).collect(Collectors.toMap(ShuttleStatusEnum::getStatus, e -> e));

    public static ShuttleStatusEnum getInstance(byte status) {
        return maps.get(status);
    }
}
