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
public class ShuttleRequest implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 类名称
     */
    private String className;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 方法描述
     */
    private String methodDescriptor;

    /**
     * 参数列表
     */
    private Object[] args;

}