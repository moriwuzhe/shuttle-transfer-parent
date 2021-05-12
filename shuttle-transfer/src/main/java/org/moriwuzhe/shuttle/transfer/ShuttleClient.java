package org.moriwuzhe.shuttle.transfer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import jdk.internal.org.objectweb.asm.Type;
import lombok.extern.slf4j.Slf4j;
import org.moriwuzhe.shuttle.common.logger.Logger;
import org.moriwuzhe.shuttle.common.logger.LoggerFactory;
import org.moriwuzhe.shuttle.transfer.codec.ShuttleCodec;
import org.moriwuzhe.shuttle.transfer.enums.ShuttleSerializableEnum;
import org.moriwuzhe.shuttle.transfer.msg.ShuttleMessage;
import org.moriwuzhe.shuttle.transfer.msg.ShuttleRequest;
import org.moriwuzhe.shuttle.transfer.msg.ShuttleResponse;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description:
 * @Author: xjp
 * @Date: 2021-05-09 23:47
 * @Version: 1.0
 */
public class ShuttleClient {

    private int heartDelay = 2000;

    private Channel channel;

    private AtomicLong atomicLong = new AtomicLong(0);

    private Map<Long, Promise<ShuttleResponse>> results = new HashMap<>();

    private Logger log = LoggerFactory.getLogger(ShuttleClient.class);

    public void connect(String ip, int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup(1))
                .channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast("codec", new ShuttleCodec())
                        .addLast("responseHandle", new ShuttleResponseHandle());
            }
        });
        channel = bootstrap.connect(ip, port).sync().channel();
        log.info("connection success !!");

        // 每隔 两秒发送心跳
        channel.eventLoop().scheduleWithFixedDelay(() -> {
            ShuttleMessage shuttleMessage = new ShuttleMessage()
                    .setId(getNextId()).setHeartbeat(true);
            channel.writeAndFlush(shuttleMessage);
        }, 0, heartDelay, TimeUnit.MILLISECONDS);
    }

    private long getNextId() {
        return atomicLong.incrementAndGet();
    }

    private ShuttleResponse invokerRemote(Class serverInterface, String methodName, String methodDescriptor, Object[] args)
            throws InterruptedException, ExecutionException, TimeoutException {

        ShuttleRequest request = new ShuttleRequest()
                .setArgs(args).setClassName(serverInterface.getName())
                .setMethodDescriptor(methodDescriptor)
                .setMethodName(methodName);
        ShuttleMessage shuttleMessage = new ShuttleMessage()
                .setId(getNextId()).setRequest(true)
                .setSerializable(ShuttleSerializableEnum.JAVA.getSerializable())
                .setTarget(request);

        DefaultPromise<ShuttleResponse> resultPromise = new DefaultPromise(channel.eventLoop());

        // 写入成功后添加 结果
        channel.writeAndFlush(shuttleMessage).addListener(future ->
                {
                    if (future.cause() != null) {
                        // 写入失败
                        resultPromise.setFailure(future.cause());
                    } else {
                        // 写入成功
                        results.put(shuttleMessage.getId(), resultPromise);
                    }
                }
        );

        return resultPromise.get(10000, TimeUnit.MILLISECONDS);
    }

    private class ShuttleResponseHandle extends
            SimpleChannelInboundHandler<ShuttleMessage> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ShuttleMessage msg) throws Exception {
            if (msg.isHeartbeat()) {
                log.info(String.format("服务端心跳返回：%s", ctx.channel().remoteAddress()));
                return;
            }

            Promise<ShuttleResponse> promise = results.remove(msg.getId());
            // 填充结果
            promise.setSuccess((ShuttleResponse) msg.getTarget());
        }
    }


    public <T> T getRemoteService(Class<T> serviceInterface) {
        assert serviceInterface.isInterface();

        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{serviceInterface},
                (Object proxy, Method method, Object[] args) -> {
                    if (Object.class.equals(method.getDeclaringClass())) {
                        return method.invoke(this, args);
                    }

                    String methodDescriptor = method.getName() + Type.getMethodDescriptor(method);
                    ShuttleResponse response = invokerRemote(serviceInterface, method.getName(), methodDescriptor, args);
                    if (response.getError() != null) {
                        throw new RuntimeException("invoke remote error：", response.getError());
                    }
                    return response.getResult();
                });
    }
}
