package org.moriwuzhe.shuttle.transfer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jdk.internal.org.objectweb.asm.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.moriwuzhe.shuttle.common.exceptions.ShuttleException;
import org.moriwuzhe.shuttle.common.logger.Logger;
import org.moriwuzhe.shuttle.common.logger.LoggerFactory;
import org.moriwuzhe.shuttle.transfer.codec.ShuttleCodec;
import org.moriwuzhe.shuttle.transfer.enums.ShuttleStatusEnum;
import org.moriwuzhe.shuttle.transfer.msg.ShuttleMessage;
import org.moriwuzhe.shuttle.transfer.msg.ShuttleRequest;
import org.moriwuzhe.shuttle.transfer.msg.ShuttleResponse;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: xjp
 * @Date: 2021-05-06 23:46
 * @Version: 1.0
 */
@Getter
public class ShuttleServer {

    private Logger log = LoggerFactory.getLogger(ShuttleServer.class);

    /**
     * shuttle
     */
    private int port;

    private int bossThreads;

    private int workThreads;

    private ThreadPoolExecutor handleThreadPoolExecutor;

    private BlockingQueue businessBlockingQueue;

    /**
     * TODO 重构 注册部分逻辑
     */
    private final Map<String, ServiceBean> register = new HashMap<>();

    private Map<String, ServiceBean> getRegister() {
        return register;
    }

    private ShuttleServer() {
    }

    @Setter
    @Accessors(chain = true)
    public static class Builder {
        /**
         * 启动的端口
         */
        private int port = 5477;
        /**
         * NioEventLoopGroup boos 线程数
         */
        private int bossThreads = 1;
        /**
         * NioEventLoopGroup work 线程数
         */
        private int workThreads = Runtime.getRuntime().availableProcessors();
        /**
         * 处理业务的线程数量
         */
        private int businessThreads = 500;
        /**
         * 处理业务的线程数量
         */
        private BlockingQueue businessBlockingQueue = new LinkedBlockingQueue<>();

        public ShuttleServer build() throws InterruptedException {
            ShuttleServer shuttleServer = new ShuttleServer();
            shuttleServer.handleThreadPoolExecutor = new ThreadPoolExecutor(businessThreads, businessThreads,
                    0L, TimeUnit.MILLISECONDS, businessBlockingQueue);
            shuttleServer.bossThreads = this.bossThreads;
            shuttleServer.port = this.port;
            shuttleServer.workThreads = this.workThreads;
            shuttleServer.businessBlockingQueue = this.businessBlockingQueue;

            shuttleServer.start();
            return shuttleServer;
        }
    }

    public void registerServer(Class serviceInterface, Object serverBean) {
        assert serviceInterface.isInterface();
        for (Method method : serviceInterface.getMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isNative(modifiers)) {
                continue;
            }
            String methodDescriptor = Type.getMethodDescriptor(method);
            String key = serviceInterface.getName() + "#" + method.getName() + methodDescriptor;
            register.put(key, new ServiceBean(method, serverBean));
        }
    }

    private static class ServiceBean {
        Method method;
        Object target;

        public ServiceBean(Method method, Object target) {
            this.method = method;
            this.target = target;
        }

        public Object invoke(Object[] args) throws Exception {
            return method.invoke(target, args);
        }
    }

    private void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(new NioEventLoopGroup(bossThreads),
                        new NioEventLoopGroup(workThreads))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                .addLast("codec", new ShuttleCodec())
                                .addLast("dispatch", new ShuttleRequestHandle());
                    }
                })
                .bind(port).sync();
        log.info("server started ..");
    }


    private class ShuttleRequestHandle extends SimpleChannelInboundHandler<ShuttleMessage> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ShuttleMessage msg) {
            if (!msg.isHeartbeat()) {
                // 业务请求, 异步处理
                handleThreadPoolExecutor.submit(() -> ctx.writeAndFlush(doHandle(msg)));
                return;
            }
            //心跳
            ctx.writeAndFlush(new ShuttleMessage()
                    .setHeartbeat(true)
                    .setRequest(false));
        }

        private Object doHandle(ShuttleMessage requestMessage) {
            ShuttleRequest request = (ShuttleRequest) requestMessage.getTarget();
            ShuttleResponse response = new ShuttleResponse();
            byte status;
            try {
                String serviceKey = request.getClassName() + "#" + request.getMethodDescriptor();
                ServiceBean serviceBean = register.get(serviceKey);
                if (serviceBean == null) {
                    throw new ShuttleException("can not found service bean , serverKey = " + serviceKey);
                }
                response.setResult(serviceBean.invoke(request.getArgs()));
                status = ShuttleStatusEnum.SUCCESS.getStatus();
            } catch (Exception e) {
                response.setError(e);
                status = ShuttleStatusEnum.ERROR.getStatus();
            }

            // 返回调用结果
            return new ShuttleMessage()
                    .setRequest(false)
                    .setHeartbeat(false)
                    .setTarget(response)
                    .setId(requestMessage.getId())
                    .setSerializable(requestMessage.getSerializable())
                    .setTwoWay(requestMessage.isTwoWay())
                    .setStatus(status);
        }
    }
}
