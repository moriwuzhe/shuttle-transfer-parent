package org.moriwuzhe.shuttle.transfer.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
  * @Description:
  * @Author: moriwuzhe
  * @Date: 2021-05-12 00:02
  * @Version: 1.0
  */
public class ShuttleByteArrayOutputStream extends OutputStream {
    protected byte[] bytes;

    protected int length;

    public ShuttleByteArrayOutputStream() {
        this(32);
    }

    public ShuttleByteArrayOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        bytes = new byte[size];
    }

    @Override
    public void write(int b) {
        int tempLength = length + 1;
        if (tempLength > bytes.length) {
            bytes = Bytes.copyOf(bytes, Math.max(bytes.length << 1, tempLength));
        }
        bytes[length] = (byte) b;
        length = tempLength;
    }

    @Override
    public void write(byte[] b, int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        int tempLength = length + len;
        if (tempLength > bytes.length) {
            bytes = Bytes.copyOf(bytes, Math.max(bytes.length << 1, tempLength));
        }
        System.arraycopy(b, off, bytes, length, len);
        length = tempLength;
    }

    public int size() {
        return length;
    }

    public void reset() {
        length = 0;
    }

    public byte[] toByteArray() {
        return Bytes.copyOf(bytes, length);
    }

    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(bytes, 0, length);
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(bytes, 0, length);
    }

    @Override
    public String toString() {
        return new String(bytes, 0, length);
    }

    public String toString(String charset) throws UnsupportedEncodingException {
        return new String(bytes, 0, length, charset);
    }
}
