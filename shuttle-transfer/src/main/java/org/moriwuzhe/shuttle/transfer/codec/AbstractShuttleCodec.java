package org.moriwuzhe.shuttle.transfer.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.ByteToMessageCodec;

/**
 * <p>
 * desc =>    |  magic   |                            flag                              | status | unique id  | body size |         <br>
 * FLAG =>    |  MAGIC   | REQUEST - TWO_WAY - HEARTBEAT - SERIALIZATION - CONTENT_TYPE | STATUS |    UID     |   SIZE    |         <br>
 * byte =>    |     2    |                             1                                |   1    |     8      |     4     |         <br>
 * <p>
 * HEADER LENGTH 16Byte
 * <p>
 * MAGIC: 2Byte
 * <p>
 * FLAG: 1Byte <br>
 * 1-1  REQUEST: 1bit => 0->request, 1->response <br>
 * 1-2  TWO_WAY: 1bit => 0->broadcast, 1->two way <br>
 * 1-3  HEARTBEAT: 1bit => 0->not heart beat, 1-> heart beat <br>
 * 1-4  SERIALIZATION: 1bit => {@link org.moriwuzhe.shuttle.transfer.enums.ShuttleSerializableEnum} <br>
 * 1-5  CONTENT_TYPE: 1bit => {@link org.moriwuzhe.shuttle.transfer.enums.ShuttleContentTypeEnum} <br>
 * 1-6  reserved <br>
 * 1-7  reserved <br>
 * 1-0  reserved <br>
 * <p>
 * STATUS: 1Byte {@link org.moriwuzhe.shuttle.transfer.enums.ShuttleStatusEnum}
 * <p>
 * REQUEST ID: 8Byte => long type
 * <p>
 * SIZE: 4Byte => int type
 * <p>
 *
 * **************BODY***************
 *
 * @Description:
 * @Author: xjp
 * @Date: 2021-05-07 0:07
 * @Version: 1.0
 */
public abstract class AbstractShuttleCodec extends ByteToMessageCodec {

    protected static final int HEADER_LENGTH = 16;

    /**
     * shuttle 魔数
     * 标识shuttle协议
     */
    protected static final short MAGIC = (short) 0x540771E;

    protected static final ByteBuf MAGIC_BUF = Unpooled.copyShort(MAGIC);

    /**
     * 1000 0000
     * 标识请求
     */
    protected static final byte REQUEST = (byte) 0x80;

    /**
     * 0100 0000
     * 标识单双向通信
     */
    protected static final byte TWO_WAY = (byte) 0x40;

    /**
     * 0010 0000
     * 标识心跳
     */
    protected static final byte HEARTBEAT = (byte) 0x20;
    /**
     * 0001 0000
     * 标识序列号方式
     */
    protected static final int SERIALIZATION = 0x10;
    /**
     * 0000 1000
     * 标识内容格式
     */
    protected static final int CONTENT_TYPE = 0x08;

    protected int indexOf(ByteBuf haystack, ByteBuf needle) {
        for (int i = haystack.readerIndex(); i < haystack.writerIndex(); i++) {
            int haystackIndex = i;
            int needleIndex;
            for (needleIndex = 0; needleIndex < needle.capacity(); needleIndex++) {
                if (haystack.getByte(haystackIndex) != needle.getByte(needleIndex)) {
                    break;
                }

                haystackIndex++;
                if (haystackIndex == haystack.writerIndex() &&
                        needleIndex != needle.capacity() - 1) {
                    return -1;
                }
            }

            if (needleIndex == needle.capacity()) {
                // Found the needle from the haystack!
                return i - haystack.readerIndex();
            }
        }
        return -1;
    }
}
