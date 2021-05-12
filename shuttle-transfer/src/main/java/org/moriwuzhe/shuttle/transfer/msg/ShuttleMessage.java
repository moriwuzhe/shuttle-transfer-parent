package org.moriwuzhe.shuttle.transfer.msg;

import org.moriwuzhe.shuttle.transfer.enums.ShuttleSerializableEnum;
import org.moriwuzhe.shuttle.transfer.enums.ShuttleStatusEnum;

import java.io.Serializable;

/**
 * @Description:
 * @Author: xjp
 * @Date: 2021-05-07 0:16
 * @Version: 1.0
 */
@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.experimental.Accessors(chain = true)
public class ShuttleMessage implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 请求报文
     */
    private boolean request;
    /**
     * 序列化方式
     */
    private ShuttleSerializableEnum serializable;
    /**
     * 是否双向通信
     */
    private boolean twoWay;
    /**
     * 心跳包
     */
    private boolean heartbeat;
    /**
     * 请求的唯一标识
     */
    private long id;
    /**
     * 状态
     */
    private ShuttleStatusEnum status;
    /**
     * 目标对象
     */
    private Object target;

    public byte getSerializable() {
        return serializable.getSerializable();
    }

    public byte getStatus() {
        return status.getStatus();
    }

    public ShuttleMessage setStatus(byte status) {
        this.status = ShuttleStatusEnum.getInstance(status);
        return this;
    }

    public ShuttleMessage setSerializable(byte serializable) {
        this.serializable = ShuttleSerializableEnum.getInstance(serializable);
        return this;
    }

    @Override
    public String toString() {
        return "ShuttleMessage{" +
                "request=" + request +
                ", serializable=" + serializable +
                ", twoWay=" + twoWay +
                ", heartbeat=" + heartbeat +
                ", id=" + id +
                ", status=" + status +
                ", target=" + target +
                '}';
    }
}
