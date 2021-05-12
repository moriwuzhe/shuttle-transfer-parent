package org.moriwuzhe.shuttle.transfer.msg;

import java.io.Serializable;

/**
 * @Description:
 * @Author: xjp
 * @Date: 2021-05-08 0:49
 * @Version: 1.0
 */
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.experimental.Accessors(chain = true)
public class ShuttleResponse implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 结果
     */
    private Object result;

    /**
     * 异常
     */
    private Throwable error;

}