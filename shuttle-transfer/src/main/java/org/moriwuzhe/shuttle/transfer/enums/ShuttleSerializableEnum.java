package org.moriwuzhe.shuttle.transfer.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: xjp
 * @Date: 2021-05-07 0:25
 * @Version: 1.0
 */
@lombok.Getter
@lombok.AllArgsConstructor
public enum ShuttleSerializableEnum {
    /**
     * java
     */
    JAVA((byte) 0),
    /**
     * json对象
     */
    JSON((byte) 1),
    /**
     * hessian2
     */
    HESSIAN2((byte) 2),
    /**
     * protobuf
     */
    PROTO_BUF((byte) 3),
    ;

    byte serializable;

    private final static Map<Byte, ShuttleSerializableEnum> maps =
            Arrays.stream(values()).collect(Collectors.toMap(ShuttleSerializableEnum::getSerializable, e -> e));

    public static ShuttleSerializableEnum getInstance(byte serializable) {
        return maps.get(serializable);
    }
}
