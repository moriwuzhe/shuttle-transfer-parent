package org.moriwuzhe.shuttle.transfer;

import org.moriwuzhe.shuttle.transfer.tests.TestService;

import java.io.IOException;

public class ClientTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        ShuttleClient client = new ShuttleClient();
        client.connect("127.0.0.1", 9982);
        TestService service = client.getRemoteService(TestService.class);
        while (true) {
            System.out.println(service.test("test arg !"));
            Thread.sleep(1000);
        }
    }
}
