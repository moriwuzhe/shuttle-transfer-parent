package org.moriwuzhe.shuttle.transfer.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.moriwuzhe.shuttle.common.exceptions.ShuttleException;
import org.moriwuzhe.shuttle.transfer.enums.ShuttleStatusEnum;
import org.moriwuzhe.shuttle.transfer.msg.ShuttleMessage;
import org.moriwuzhe.shuttle.transfer.enums.ShuttleSerializableEnum;
import org.moriwuzhe.shuttle.transfer.utils.Bytes;

import java.io.*;
import java.util.List;

/**
 * @Description:
 * @Author: xjp
 * @Date: 2021-05-07 0:07
 * @Version: 1.0
 */
public class ShuttleCodec extends AbstractShuttleCodec {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf byteBuf) throws Exception {
        if (!(msg instanceof ShuttleMessage)) {
            throw new IllegalArgumentException();
        }
        doEncode((ShuttleMessage) msg, byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
        ShuttleMessage message = doDecode(in);
        if (message != null) {
            out.add(message);
        }
    }

    private void doEncode(ShuttleMessage msg, ByteBuf byteBuf) {
        byte[] header = new byte[HEADER_LENGTH];
        // magic need 2 Byte and fill in the index 0 and 1
        Bytes.short2bytes(MAGIC, header);
        // flag need 1 Byte and index start with 2
        header[2] = msg.getSerializable();
        if (msg.isRequest()) {
            header[2] |= REQUEST;
        }
        if (msg.isTwoWay()) {
            header[2] |= TWO_WAY;
        }
        if (msg.isHeartbeat()) {
            header[2] |= HEARTBEAT;
        }
        // status need 1 Byte and index start with 3
        if (!msg.isRequest()) {
            header[3] = msg.getStatus();
        }
        // id need 8 Byte and index start with 4
        Bytes.long2bytes(msg.getId(), header, 4);

        byte[] body = new byte[0];
        if (!msg.isHeartbeat()) {
            body = serialize(msg.getSerializable(), msg.getTarget());
        }
        // body need 4 Byte and  index start with 12
        Bytes.int2bytes(body.length, header, 12);

        //write data header and body
        byteBuf.writeBytes(header);
        byteBuf.writeBytes(body);
    }

    private ShuttleMessage doDecode(ByteBuf byteBuf) {
        // magic
        int index = indexOf(byteBuf, MAGIC_BUF);
        if (index < 0) {
            // magic not found need more byte buf
            return null;
        }
        if (!byteBuf.isReadable(index + HEADER_LENGTH)) {
            // magic and header byte not enough, need more byte buf
            return null;
        }
        byte[] header = new byte[HEADER_LENGTH];
        ByteBuf slice = byteBuf.slice();
        slice.readBytes(header);
        int length = Bytes.bytes2int(header, 12);

        if (!byteBuf.isReadable(index + HEADER_LENGTH + length)) {
            // body byte not enough, need more byte buf
            return null;
        }
        ShuttleMessage message = new ShuttleMessage()
                .setId(Bytes.bytes2long(header, 4))
                .setRequest((header[2] & REQUEST) != 0)
                .setTwoWay((header[2] & TWO_WAY) != 0)
                .setHeartbeat((header[2] & HEARTBEAT) != 0)
                .setSerializable((byte) (header[2] & SERIALIZATION))
                .setStatus(header[3]);

        if (message.isHeartbeat()) {
            // heart beat return success
            message.setStatus(ShuttleStatusEnum.SUCCESS.getStatus());
        } else{
            // deserialize content
            // TODO add deserialize content by content type
            byte[] content = new byte[length];
            slice.readBytes(content);
            message.setTarget(deserialize(message.getSerializable(), content));
        }
        byteBuf.skipBytes(index + HEADER_LENGTH + length);
        return message;
    }

    private byte[] serialize(byte serializable, Object target) {
        if (serializable == ShuttleSerializableEnum.JAVA.getSerializable()) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream stream = new ObjectOutputStream(out);
                stream.writeObject(target);
                return out.toByteArray();
            } catch (IOException e) {
                throw new ShuttleException("serialize error", e);
            }
        }
        throw new ShuttleException("not found serializable!");
    }

    private Object deserialize(byte serializable, byte[] bytes) {
        if (serializable == ShuttleSerializableEnum.JAVA.getSerializable()) {
            try {
                ObjectInputStream stream =
                        new ObjectInputStream(new ByteArrayInputStream(bytes));
                return stream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        throw new UnsupportedOperationException();
    }
}
