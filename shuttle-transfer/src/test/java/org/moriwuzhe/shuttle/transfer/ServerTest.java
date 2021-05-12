package org.moriwuzhe.shuttle.transfer;

import org.moriwuzhe.shuttle.transfer.tests.TestService;
import org.moriwuzhe.shuttle.transfer.tests.impl.TestServiceImpl;

public class ServerTest {

    public static void main(String[] args) throws Exception {
        ShuttleServer server = new ShuttleServer.Builder().setPort(9982).build();
        server.registerServer(TestService.class, new TestServiceImpl());
        System.in.read();
    }
}
