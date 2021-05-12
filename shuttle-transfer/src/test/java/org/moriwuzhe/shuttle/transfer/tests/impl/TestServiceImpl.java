package org.moriwuzhe.shuttle.transfer.tests.impl;

import org.moriwuzhe.shuttle.transfer.tests.TestService;

public class TestServiceImpl implements TestService {

    @Override
    public String test(String arg) {
        return "test result ..";
    }
}
